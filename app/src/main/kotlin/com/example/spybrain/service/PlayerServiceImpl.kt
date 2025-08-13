package com.example.spybrain.service

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.spybrain.domain.service.IPlayerService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация IPlayerService на базе ExoPlayer без привязки к Android Service.
 * Используется во ViewModel для управляемого воспроизведения.
 */
@Singleton
class PlayerServiceImpl @Inject constructor(
    @ApplicationContext context: Context
) : IPlayerService {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            /* handleAudioFocus= */ true
        )
        repeatMode = Player.REPEAT_MODE_OFF
        playWhenReady = false
        prepare()
    }
    private val mediaSession: MediaSession = MediaSession.Builder(context, exoPlayer).build()

    override fun play(url: String) {
        if (url.isBlank()) return
        try {
            // Поддерживаем android.resource:// для оффлайн треков из res/raw
            exoPlayer.setMediaItem(MediaItem.fromUri(url))
            exoPlayer.prepare()
            exoPlayer.play()
        } catch (_: Exception) {
            // Fail silently; ViewModel покажет ошибку по таймауту
        }
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun isPlaying(): Boolean = exoPlayer.isPlaying

    override fun release() {
        mediaSession.release()
        exoPlayer.release()
    }

    override fun getCurrentPosition(): Long = exoPlayer.currentPosition

    override fun getDuration(): Long = exoPlayer.duration

    override fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }
}

