package com.example.spybrain.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.audiofx.PresetReverb
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.spybrain.MainActivity
import com.example.spybrain.R
import timber.log.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import androidx.media3.common.C

class AmbientMusicService : Service() {
    
    private val binder = LocalBinder()
    private var exoPlayer: ExoPlayer? = null
    private var audioSink: AudioSink? = null
    private var presetReverb: PresetReverb? = null
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var fadeJob: Job? = null
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "ambient_music_channel"
        private const val FADE_DURATION_MS = 3000L
    }
    
    inner class LocalBinder : Binder() {
        fun getService(): AmbientMusicService = this@AmbientMusicService
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializePlayer()
        Timber.d("AmbientMusicService created")
    }
    
    private fun initializePlayer() {
        try {
            exoPlayer = ExoPlayer.Builder(this)
                .build()
                .apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(C.USAGE_MEDIA)
                            .setContentType(C.CONTENT_TYPE_MUSIC)
                            .build(),
                        true
                    )
                    volume = 0.3f
                    repeatMode = Player.REPEAT_MODE_ALL
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            when (playbackState) {
                                Player.STATE_READY -> {
                                    Timber.d("Player ready")
                                    setupAudioEffects()
                                }
                                Player.STATE_ENDED -> Timber.d("Player ended")
                                Player.STATE_BUFFERING -> Timber.d("Player buffering")
                                Player.STATE_IDLE -> Timber.d("Player idle")
                            }
                        }
                        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                            Timber.e(error, "Player error: ${error.message}")
                        }
                    })
                }
            setupReverb()
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize player")
        }
    }
    
    private fun setupReverb() {
        try {
            val audioSessionId = exoPlayer?.audioSessionId ?: return
            presetReverb = PresetReverb(1, audioSessionId).apply {
                preset = PresetReverb.PRESET_LARGEHALL
                enabled = true
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to setup reverb")
        }
    }
    
    private fun setupAudioEffects() {
        try {
            presetReverb?.let { reverb ->
                reverb.enabled = true
                reverb.preset = PresetReverb.PRESET_LARGEHALL
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to setup audio effects")
        }
    }
    
    fun playAmbientMusic(trackId: String) {
        try {
            val mediaItem = when (trackId) {
                "nature" -> MediaItem.fromUri("asset:///audio/ambient_nature.mp3")
                "ocean" -> MediaItem.fromUri("asset:///audio/ambient_ocean.mp3")
                "forest" -> MediaItem.fromUri("asset:///audio/ambient_forest.mp3")
                "rain" -> MediaItem.fromUri("asset:///audio/ambient_rain.mp3")
                "fire" -> MediaItem.fromUri("asset:///audio/ambient_fire.mp3")
                else -> MediaItem.fromUri("asset:///audio/ambient_nature.mp3")
            }
            
            exoPlayer?.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
            }
            
            startForeground(NOTIFICATION_ID, createNotification())
            Timber.d("Started playing ambient music: $trackId")
            
        } catch (e: Exception) {
            Timber.e(e, "Failed to play ambient music")
        }
    }
    
    fun stopAmbientMusic() {
        try {
            fadeJob?.cancel()
            fadeJob = serviceScope.launch {
                fadeOutAndStop()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop ambient music")
        }
    }
    
    private suspend fun fadeOutAndStop() {
        try {
            val player = exoPlayer ?: return
            val initialVolume = player.volume
            val steps = 30
            val volumeStep = initialVolume / steps
            val stepDuration = FADE_DURATION_MS / steps
            
            repeat(steps) { step ->
                val newVolume = initialVolume - (volumeStep * step)
                player.volume = newVolume.coerceAtLeast(0f)
                kotlinx.coroutines.delay(stepDuration)
            }
            
            player.stop()
            stopForeground(true)
            stopSelf()
            
        } catch (e: Exception) {
            Timber.e(e, "Failed to fade out")
        }
    }
    
    fun setVolume(volume: Float) {
        try {
            exoPlayer?.volume = volume.coerceIn(0f, 1f)
        } catch (e: Exception) {
            Timber.e(e, "Failed to set volume")
        }
    }
    
    fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying == true
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Ambient Music",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Фоновая музыка для медитации"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Фоновая музыка")
            .setContentText("Воспроизводится приятная музыка для медитации")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onDestroy() {
        try {
            fadeJob?.cancel()
            presetReverb?.release()
            exoPlayer?.release()
            serviceScope.cancel()
            Timber.d("AmbientMusicService destroyed")
        } catch (e: Exception) {
            Timber.e(e, "Error destroying service")
        }
        super.onDestroy()
    }
} 