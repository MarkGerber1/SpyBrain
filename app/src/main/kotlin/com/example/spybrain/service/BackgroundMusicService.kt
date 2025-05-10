package com.example.spybrain.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BackgroundMusicService : Service() {
    @Inject
    lateinit var exoPlayer: ExoPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val url = intent.getStringExtra(EXTRA_URL)
                if (!url.isNullOrEmpty()) {
                    exoPlayer.setMediaItem(MediaItem.fromUri(url))
                    // exoPlayer.isLooping = true // ExoPlayer не поддерживает isLooping напрямую
                    exoPlayer.prepare()
                    exoPlayer.play()
                }
            }
            ACTION_STOP -> {
                exoPlayer.stop()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        exoPlayer.stop()
        exoPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_PLAY = "com.example.spybrain.service.BACKGROUND_MUSIC_PLAY"
        const val ACTION_STOP = "com.example.spybrain.service.BACKGROUND_MUSIC_STOP"
        const val EXTRA_URL = "com.example.spybrain.service.EXTRA_URL"
    }
} 