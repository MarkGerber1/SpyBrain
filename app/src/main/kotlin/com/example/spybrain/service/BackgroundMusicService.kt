package com.example.spybrain.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BackgroundMusicService : Service() {
    @Inject
    lateinit var exoPlayer: ExoPlayer

    override fun onCreate() {
        super.onCreate()
        setupExoPlayer()
    }

    private fun setupExoPlayer() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                Log.e("BackgroundMusicService", "Playback error: ${error.message}", error)
                // РњРѕР¶РЅРѕ РґРѕР±Р°РІРёС‚СЊ СѓРІРµРґРѕРјР»РµРЅРёРµ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ РѕР± РѕС€РёР±РєРµ
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    // Р—Р°С†РёРєР»РёРІР°РµРј РјСѓР·С‹РєСѓ
                    exoPlayer.seekTo(0)
                    exoPlayer.play()
                }
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val url = intent.getStringExtra(EXTRA_URL)
                if (!url.isNullOrEmpty()) {
                    try {
                        exoPlayer.setMediaItem(MediaItem.fromUri(url))
                        exoPlayer.prepare()
                        exoPlayer.play()
                        Log.d("BackgroundMusicService", "Started playing: $url")
                    } catch (e: Exception) {
                        Log.e("BackgroundMusicService", "Error playing music: ${e.message}", e)
                    }
                } else {
                    Log.w("BackgroundMusicService", "URL is null or empty")
                }
            }
            ACTION_STOP -> {
                try {
                    exoPlayer.stop()
                    Log.d("BackgroundMusicService", "Music stopped")
                } catch (e: Exception) {
                    Log.e("BackgroundMusicService", "Error stopping music: ${e.message}", e)
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        try {
            exoPlayer.stop()
            exoPlayer.release()
        } catch (e: Exception) {
            Log.e("BackgroundMusicService", "Error in onDestroy: ${e.message}", e)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_PLAY = "com.example.spybrain.service.BACKGROUND_MUSIC_PLAY"
        const val ACTION_STOP = "com.example.spybrain.service.BACKGROUND_MUSIC_STOP"
        const val EXTRA_URL = "com.example.spybrain.service.EXTRA_URL"
    }
}
