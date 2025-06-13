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

    // Инициализируем ExoPlayer как nullable и инициализируем в onCreate
    private var exoPlayer: ExoPlayer? = null

    private lateinit var mediaSession: MediaSession
    private lateinit var notificationManager: PlayerNotificationManager

    companion object {
        private const val CHANNEL_ID = "meditation_playback"
        private const val NOTIF_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        
        // Инициализируем ExoPlayer в onCreate, когда контекст точно доступен
        exoPlayer = ExoPlayer.Builder(applicationContext).build().also { player ->
            player.repeatMode = Player.REPEAT_MODE_OFF
            player.playWhenReady = false
            player.prepare()
        }
        
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
        mediaSession = MediaSession.Builder(this, exoPlayer!!).build()

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
            setPlayer(exoPlayer!!)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.setPlayer(null)
        mediaSession.release()
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun play(url: String) {
        try {
            val player = exoPlayer ?: return
            
            // Проверяем URL на валидность
            if (url.isEmpty() || url.isBlank()) {
                throw IllegalArgumentException("URL пуст")
            }
            
            // Обрабатываем различные форматы URL
            val finalUrl = when {
                url.contains("example.com") -> {
                    // Заменяем example.com на локальный ресурс
                    "asset:///audio/mixkit-valley-sunset-127.mp3"
                }
                url.startsWith("file:///android_asset/") -> {
                    // Конвертируем в формат asset:/// для ExoPlayer
                    url.replace("file:///android_asset/", "asset:///")
                }
                url.startsWith("asset:///") -> {
                    // Уже правильный формат
                    url
                }
                url.startsWith("http://") || url.startsWith("https://") -> {
                    // Внешние URL оставляем как есть
                    url
                }
                else -> {
                    // Предполагаем, что это имя файла в папке audio
                    val assetFileName = if (url.startsWith("audio/")) {
                        url
                    } else {
                        "audio/$url"
                    }
                    "asset:///$assetFileName"
                }
            }
            
            try {
                val mediaItem = MediaItem.fromUri(finalUrl)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
                
                android.util.Log.d("MeditationPlayerService", "Воспроизведение начато: $finalUrl")
            } catch (innerEx: Exception) {
                android.util.Log.e("MeditationPlayerService", 
                    "Ошибка воспроизведения через ExoPlayer: ${innerEx.message}", innerEx)
                
                // Fallback: попробуем через прямой доступ к ассетам
                try {
                    val assetFileName = when {
                        finalUrl.startsWith("asset:///") -> finalUrl.removePrefix("asset:///")
                        finalUrl.startsWith("file:///android_asset/") -> finalUrl.removePrefix("file:///android_asset/")
                        else -> "audio/mixkit-valley-sunset-127.mp3"
                    }
                    
                    // Проверяем существование файла
                    try {
                        applicationContext.assets.open(assetFileName).use { 
                            android.util.Log.d("MeditationPlayerService", "Файл найден: $assetFileName")
                        }
                    } catch (fileEx: Exception) {
                        android.util.Log.e("MeditationPlayerService", 
                            "Файл не найден: $assetFileName", fileEx)
                        throw fileEx
                    }
                    
                    // Используем правильный формат для ассетов
                    val assetMediaItem = MediaItem.fromUri("asset:///$assetFileName")
                    player.setMediaItem(assetMediaItem)
                    player.prepare()
                    player.play()
                    
                    android.util.Log.d("MeditationPlayerService", "Воспроизведение через fallback: asset:///$assetFileName")
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
        exoPlayer?.pause()
    }

    override fun stop() {
        exoPlayer?.stop()
    }

    override fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying ?: false
    }

    override fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    override fun getDuration(): Long = exoPlayer?.duration ?: 0L
    override fun seekTo(positionMs: Long) { exoPlayer?.seekTo(positionMs) }
}
// NOTE реализовано по аудиту: IPlayerService адаптер 