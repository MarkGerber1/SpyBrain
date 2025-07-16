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

    // РРЅРёС†РёР°Р»РёР·РёСЂСѓРµРј ExoPlayer РєР°Рє nullable Рё РёРЅРёС†РёР°Р»РёР·РёСЂСѓРµРј РІ onCreate
    private var exoPlayer: ExoPlayer? = null

    private lateinit var mediaSession: MediaSession
    private lateinit var notificationManager: PlayerNotificationManager

    companion object {
        private const val CHANNEL_ID = "meditation_playback"
        private const val NOTIF_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()

        // РРЅРёС†РёР°Р»РёР·РёСЂСѓРµРј ExoPlayer РІ onCreate, РєРѕРіРґР° РєРѕРЅС‚РµРєСЃС‚ С‚РѕС‡РЅРѕ РґРѕСЃС‚СѓРїРµРЅ
        exoPlayer = ExoPlayer.Builder(applicationContext).build().also { player ->
            player.repeatMode = Player.REPEAT_MODE_OFF
            player.playWhenReady = false
            player.prepare()
        }

        // РЎРѕР·РґР°РµРј РєР°РЅР°Р» РґР»СЏ СѓРІРµРґРѕРјР»РµРЅРёР№
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Р’РѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРµ РјРµРґРёС‚Р°С†РёРё",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        // РРЅРёС†РёР°Р»РёР·РёСЂСѓРµРј MediaSession
        mediaSession = MediaSession.Builder(this, exoPlayer!!).build()

        // РќР°СЃС‚СЂР°РёРІР°РµРј СѓРІРµРґРѕРјР»РµРЅРёРµ
        notificationManager = PlayerNotificationManager.Builder(
            this, NOTIF_ID, CHANNEL_ID
        ).setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): CharSequence =
                player.currentMediaItem?.mediaMetadata?.title ?: "РњРµРґРёС‚Р°С†РёСЏ"

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

            // РџСЂРѕРІРµСЂСЏРµРј URL РЅР° РІР°Р»РёРґРЅРѕСЃС‚СЊ
            if (url.isEmpty() || url.isBlank()) {
                throw IllegalArgumentException("URL РїСѓСЃС‚")
            }

            // РћР±СЂР°Р±Р°С‚С‹РІР°РµРј СЂР°Р·Р»РёС‡РЅС‹Рµ С„РѕСЂРјР°С‚С‹ URL
            val finalUrl = when {
                url.contains("example.com") -> {
                    // Р—Р°РјРµРЅСЏРµРј example.com РЅР° Р»РѕРєР°Р»СЊРЅС‹Р№ СЂРµСЃСѓСЂСЃ
                    "asset:///audio/mixkit-valley-sunset-127.mp3"
                }
                url.startsWith("file:///android_asset/") -> {
                    // РљРѕРЅРІРµСЂС‚РёСЂСѓРµРј РІ С„РѕСЂРјР°С‚ asset:/// РґР»СЏ ExoPlayer
                    url.replace("file:///android_asset/", "asset:///")
                }
                url.startsWith("asset:///") -> {
                    // РЈР¶Рµ РїСЂР°РІРёР»СЊРЅС‹Р№ С„РѕСЂРјР°С‚
                    url
                }
                url.startsWith("http://") || url.startsWith("https://") -> {
                    // Р’РЅРµС€РЅРёРµ URL РѕСЃС‚Р°РІР»СЏРµРј РєР°Рє РµСЃС‚СЊ
                    url
                }
                else -> {
                    // РџСЂРµРґРїРѕР»Р°РіР°РµРј, С‡С‚Рѕ СЌС‚Рѕ РёРјСЏ С„Р°Р№Р»Р° РІ РїР°РїРєРµ audio
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

                android.util.Log.d("MeditationPlayerService", "Р’РѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРµ РЅР°С‡Р°С‚Рѕ: $finalUrl")
            } catch (innerEx: Exception) {
                android.util.Log.e("MeditationPlayerService",
                    "РћС€РёР±РєР° РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёСЏ С‡РµСЂРµР· ExoPlayer: ${innerEx.message}", innerEx)

                // Fallback: РїРѕРїСЂРѕР±СѓРµРј С‡РµСЂРµР· РїСЂСЏРјРѕР№ РґРѕСЃС‚СѓРї Рє Р°СЃСЃРµС‚Р°Рј
                try {
                    val assetFileName = when {
                        finalUrl.startsWith("asset:///") -> finalUrl.removePrefix("asset:///")
                        finalUrl.startsWith("file:///android_asset/") -> finalUrl.removePrefix("file:///android_asset/")
                        else -> "audio/mixkit-valley-sunset-127.mp3"
                    }

                    // РџСЂРѕРІРµСЂСЏРµРј СЃСѓС‰РµСЃС‚РІРѕРІР°РЅРёРµ С„Р°Р№Р»Р°
                    try {
                        applicationContext.assets.open(assetFileName).use {
                            android.util.Log.d("MeditationPlayerService", "Р¤Р°Р№Р» РЅР°Р№РґРµРЅ: $assetFileName")
                        }
                    } catch (fileEx: Exception) {
                        android.util.Log.e("MeditationPlayerService",
                            "Р¤Р°Р№Р» РЅРµ РЅР°Р№РґРµРЅ: $assetFileName", fileEx)
                        throw fileEx
                    }

                    // РСЃРїРѕР»СЊР·СѓРµРј РїСЂР°РІРёР»СЊРЅС‹Р№ С„РѕСЂРјР°С‚ РґР»СЏ Р°СЃСЃРµС‚РѕРІ
                    val assetMediaItem = MediaItem.fromUri("asset:///$assetFileName")
                    player.setMediaItem(assetMediaItem)
                    player.prepare()
                    player.play()

                    android.util.Log.d("MeditationPlayerService", "Р’РѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРµ С‡РµСЂРµР· fallback: asset:///$assetFileName")
                } catch (assetEx: Exception) {
                    android.util.Log.e("MeditationPlayerService",
                        "РћС€РёР±РєР° РґРѕСЃС‚СѓРїР° Рє Р°СЃСЃРµС‚Сѓ: ${assetEx.message}", assetEx)
                    throw assetEx
                }
            }
        } catch (e: Exception) {
            // Р›РѕРіРёСЂСѓРµРј РѕС€РёР±РєСѓ, РЅРѕ РЅРµ РєСЂР°С€РёРј РїСЂРёР»РѕР¶РµРЅРёРµ
            android.util.Log.e("MeditationPlayerService", "РћС€РёР±РєР° РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёСЏ: ${e.message}", e)

            // РњРѕР¶РЅРѕ РїРѕСЃР»Р°С‚СЊ СѓРІРµРґРѕРјР»РµРЅРёРµ С‡РµСЂРµР· СЃРёСЃС‚РµРјРЅС‹Р№ СЃРµСЂРІРёСЃ
            try {
                val context = this@MeditationPlayerService
                android.widget.Toast.makeText(context,
                    "РќРµ СѓРґР°Р»РѕСЃСЊ РІРѕСЃРїСЂРѕРёР·РІРµСЃС‚Рё РјРµРґРёС‚Р°С†РёСЋ: ${e.message}",
                    android.widget.Toast.LENGTH_SHORT).show()
            } catch (toastError: Exception) {
                // РРіРЅРѕСЂРёСЂСѓРµРј РѕС€РёР±РєРё СЃРѕР·РґР°РЅРёСЏ Toast
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
// NOTE СЂРµР°Р»РёР·РѕРІР°РЅРѕ РїРѕ Р°СѓРґРёС‚Сѓ: IPlayerService Р°РґР°РїС‚РµСЂ
