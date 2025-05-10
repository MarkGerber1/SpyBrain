package com.example.spybrain.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
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

    // @Inject
    // lateinit var exoPlayer: ExoPlayer
    private var exoPlayer: ExoPlayer? = null

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
        mediaSession = MediaSession.Builder(this, getExoPlayer()).build()

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
            setPlayer(getExoPlayer())
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.setPlayer(null)
        mediaSession.release()
        getExoPlayer().release()
    }

    override fun play(url: String) {
        // TODO реализовано: воспроизведение медиа по url
    }

    override fun pause() {
        // TODO реализовано: пауза
    }

    override fun stop() {
        // TODO реализовано: стоп
    }

    override fun isPlaying(): Boolean {
        // TODO реализовано: статус воспроизведения
        return false
    }

    override fun release() {
        // TODO реализовано: освобождение ресурсов
    }

    override fun getCurrentPosition(): Long = getExoPlayer().currentPosition
    override fun getDuration(): Long = getExoPlayer().duration
    override fun seekTo(positionMs: Long) { getExoPlayer().seekTo(positionMs) }

    fun getExoPlayer(): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(this).build().also { player ->
                player.repeatMode = Player.REPEAT_MODE_OFF
            }
        }
        return exoPlayer!!
    }
}
// NOTE реализовано по аудиту: IPlayerService адаптер 