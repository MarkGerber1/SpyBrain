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
    private var currentTrackId: String? = null

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var fadeJob: Job? = null

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "ambient_music_channel"
        private const val FADE_DURATION_MS = 3000L

        const val ACTION_PLAY: String = "com.example.spybrain.service.AMBIENT_MUSIC_PLAY"
        const val ACTION_STOP: String = "com.example.spybrain.service.AMBIENT_MUSIC_STOP"
        const val ACTION_SET_VOLUME: String = "com.example.spybrain.service.AMBIENT_MUSIC_SET_VOLUME"
        const val EXTRA_TRACK_ID: String = "com.example.spybrain.service.EXTRA_TRACK_ID"
        const val EXTRA_VOLUME: String = "com.example.spybrain.service.EXTRA_VOLUME"
    }

    inner class LocalBinder : Binder() {
        fun getService(): AmbientMusicService = this@AmbientMusicService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        // Стартуем foreground немедленно (<=5с от запуска)
        val notif = createNotification()
        startForeground(NOTIFICATION_ID, notif)
        initializePlayer()
        Timber.i("AmbientMusicService onCreate: startForeground done")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val trackId = intent.getStringExtra(EXTRA_TRACK_ID) ?: ""
                if (trackId.isNotEmpty()) {
                    // Idempotent play: если уже играет тот же трек — ничего не делаем
                    val isSameTrack = currentTrackId == trackId
                    if (isSameTrack && (exoPlayer?.isPlaying == true)) {
                        Timber.d("AmbientMusicService: same track '$trackId' already playing, skip restart")
                        return START_STICKY
                    }
                    playAmbientMusic(trackId)
                }
            }
            ACTION_STOP -> {
                stopAmbientMusic()
            }
            ACTION_SET_VOLUME -> {
                val volume = intent.getFloatExtra(EXTRA_VOLUME, 0.5f)
                setVolume(volume)
            }
        }
        return START_STICKY
    }

    private fun initializePlayer() {
        try {
            exoPlayer = ExoPlayer.Builder(this)
                .build()
                .apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(C.USAGE_MEDIA)
                            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
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
        // Temporarily disable reverb effects to prevent crashes
        Timber.d("Reverb effects disabled for stability")
        return
        
        /*
        try {
            val audioSessionId = exoPlayer?.audioSessionId ?: return
            
            // Check if PresetReverb is supported
            if (!PresetReverb.isAvailable()) {
                Timber.d("PresetReverb not available on this device")
                return
            }
            
            presetReverb = PresetReverb(1, audioSessionId).apply {
                preset = PresetReverb.PRESET_LARGEHALL
                enabled = true
            }
            Timber.d("PresetReverb setup successful")
        } catch (e: Exception) {
            Timber.e(e, "Failed to setup reverb - continuing without reverb")
            presetReverb = null
        }
        */
    }

    private fun setupAudioEffects() {
        // Audio effects disabled for stability
        Timber.d("Audio effects disabled for stability")
        return
        
        /*
        try {
            presetReverb?.let { reverb ->
                if (reverb.hasControl) {
                    reverb.enabled = true
                    reverb.preset = PresetReverb.PRESET_LARGEHALL
                    Timber.d("Audio effects applied successfully")
                } else {
                    Timber.d("No control over reverb effect")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to setup audio effects - continuing without effects")
            presetReverb?.release()
            presetReverb = null
        }
        */
    }

    fun playAmbientMusic(trackId: String) {
        try {
            // Если уже подготовлен нужный трек и не играет — просто запускаем
            if (currentTrackId == trackId && (exoPlayer?.isPlaying == false) && (exoPlayer?.mediaItemCount ?: 0) > 0) {
                exoPlayer?.playWhenReady = true
                exoPlayer?.play()
                startForeground(NOTIFICATION_ID, createNotification())
                Timber.i("AmbientMusicService: Resumed ambient music: $trackId")
                return
            }
            // Используем реальные ресурсы из res/raw (см. список mixkit_*.mp3)
            val pkg = packageName
            val androidRes = { name: String -> "android.resource://$pkg/raw/$name" }
            val mediaItem = when (trackId) {
                "nature" -> MediaItem.fromUri(androidRes("mixkit_spirit_in_the_woods_139"))
                "water", "ocean" -> MediaItem.fromUri(androidRes("mixkit_chillax_655"))
                "space" -> MediaItem.fromUri(androidRes("mixkit_staring_at_the_night_sky_168"))
                "air" -> MediaItem.fromUri(androidRes("mixkit_valley_sunset_127"))
                "relax", "fire" -> MediaItem.fromUri(androidRes("mixkit_relaxation_05_749"))
                else -> MediaItem.fromUri(androidRes("mixkit_spirit_in_the_woods_139"))
            }

            exoPlayer?.apply {
                stop()
                clearMediaItems()
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
                play()
            }

            // Обновим уведомление (если надо) и залогируем старт
            startForeground(NOTIFICATION_ID, createNotification())
            Timber.i("AmbientMusicService: Started playing ambient music: $trackId")
            currentTrackId = trackId

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
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
                description = getString(R.string.settings_ambient_music)
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
            .setContentTitle(getString(R.string.settings_ambient_music))
            .setContentText(getString(R.string.settings_ambient_music))
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

    override fun onTaskRemoved(rootIntent: Intent?) {
        try {
            exoPlayer?.stop()
            exoPlayer?.release()
            stopSelf()
            Timber.d("AmbientMusicService onTaskRemoved: stopped and released")
        } catch (e: Exception) {
            Timber.e(e, "Error onTaskRemoved cleanup")
        }
        super.onTaskRemoved(rootIntent)
    }
}
