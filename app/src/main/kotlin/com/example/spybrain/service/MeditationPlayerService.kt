package com.example.spybrain.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.spybrain.domain.service.IPlayerService

@AndroidEntryPoint
class MeditationPlayerService : MediaSessionService(), IPlayerService {

    // Инициализируем ExoPlayer с использованием lazy делегата, 
    // чтобы инициализировать его при первом обращении и избежать проблем с null
    private val exoPlayer: ExoPlayer by lazy { 
        ExoPlayer.Builder(applicationContext).build().also { player ->
            player.repeatMode = Player.REPEAT_MODE_OFF
            player.playWhenReady = false
            player.prepare()
        }
    }

    private lateinit var mediaSession: MediaSession
    private lateinit var notificationManager: PlayerNotificationManager

    companion object {
        private const val CHANNEL_ID = "meditation_playback"
        private const val NOTIF_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        // Создаем канал для уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Воспроизведение медитации",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
        // Инициализируем MediaSession
        mediaSession = MediaSession.Builder(this, exoPlayer).build()

        // Настраиваем уведомление
        notificationManager = PlayerNotificationManager.Builder(
            this, NOTIF_ID, CHANNEL_ID
        ).setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): CharSequence =
                player.currentMediaItem?.mediaMetadata?.title ?: "Медитация"

            override fun createCurrentContentIntent(player: Player) = null

            override fun getCurrentContentText(player: Player) = null

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ) = null
        }).setNotificationListener(object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean
            ) {
                startForeground(notificationId, notification)
            }

            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                stopSelf()
            }
        }).build().apply {
            setPlayer(exoPlayer)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.setPlayer(null)
        mediaSession.release()
        exoPlayer.release()
    }

    override fun play(url: String) {
        try {
            // Проверяем URL на валидность
            if (url.isEmpty() || url.isBlank()) {
                throw IllegalArgumentException("URL пуст")
            }
            
            // Заменяем example.com на локальный ресурс для тестирования
            val finalUrl = if (url.contains("example.com") || url.startsWith("asset:///")) {
                // Извлекаем имя файла из URL если оно начинается с asset:///
                val assetFileName = if (url.startsWith("asset:///")) {
                    url.removePrefix("asset:///")
                } else {
                    "audio/mixkit-valley-sunset-127.mp3" // Запасной вариант
                }
                
                // Используем правильный формат URL для ассетов в ExoPlayer
                "asset:///$assetFileName"
            } else {
                url
            }
            
            try {
                val mediaItem = MediaItem.fromUri(finalUrl)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
            } catch (innerEx: Exception) {
                // Если не сработал обычный URL, попробуем через AssetDataSource
                try {
                    val assetFileName = if (finalUrl.startsWith("asset:///")) {
                        finalUrl.removePrefix("asset:///")
                    } else {
                        "audio/mixkit-valley-sunset-127.mp3"
                    }
                    
                    // Используем прямой доступ через контекст к ассетам
                    val assetFd = applicationContext.assets.openFd(assetFileName)
                    val mediaItem = MediaItem.fromUri("file:///android_asset/$assetFileName")
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.play()
                } catch (assetEx: Exception) {
                    android.util.Log.e("MeditationPlayerService", 
                        "Ошибка доступа к ассету: ${assetEx.message}", assetEx)
                    throw assetEx
                }
            }
        } catch (e: Exception) {
            // Логируем ошибку, но не крашим приложение
            android.util.Log.e("MeditationPlayerService", "Ошибка воспроизведения: ${e.message}", e)
            
            // Можно послать уведомление через системный сервис
            try {
                val context = this@MeditationPlayerService
                android.widget.Toast.makeText(context, 
                    "Не удалось воспроизвести медитацию: ${e.message}", 
                    android.widget.Toast.LENGTH_SHORT).show()
            } catch (toastError: Exception) {
                // Игнорируем ошибки создания Toast
            }
        }
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    override fun release() {
        exoPlayer.release()
    }

    override fun getCurrentPosition(): Long = exoPlayer.currentPosition
    override fun getDuration(): Long = exoPlayer.duration
    override fun seekTo(positionMs: Long) { exoPlayer.seekTo(positionMs) }
}
// NOTE реализовано по аудиту: IPlayerService адаптер 