package com.example.spybrain.service

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import timber.log.Timber
import com.example.spybrain.domain.service.IPlayerService
import com.example.spybrain.R
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

    private val appContext: Context = context

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
        addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> Timber.d("PlayerService: STATE_IDLE")
                    Player.STATE_BUFFERING -> Timber.d("PlayerService: STATE_BUFFERING")
                    Player.STATE_READY -> Timber.d("PlayerService: STATE_READY")
                    Player.STATE_ENDED -> Timber.d("PlayerService: STATE_ENDED")
                }
            }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                Timber.e(error, "PlayerService: onPlayerError ${'$'}{error.message}")
            }
        })
        prepare()
    }
    private val mediaSession: MediaSession = MediaSession.Builder(context, exoPlayer).build()

    override fun play(url: String) {
        if (url.isBlank()) return
        try {
            var finalUrl = when {
                url.startsWith("android.resource://") -> url
                url.startsWith("asset:///") -> url
                url.startsWith("http://") || url.startsWith("https://") -> url
                url.startsWith("audio/") -> "asset:///$url"
                else -> url
            }
            // Специальная обработка android.resource://.../raw/<name> → Uri c resId (устойчиво к debug suffix)
            if (finalUrl.startsWith("android.resource://") && finalUrl.contains("/raw/")) {
                try {
                    val name = finalUrl.substringAfter("/raw/").substringBefore('/')
                    // 1) Сначала пробуем через R.raw рефлексию — не зависит от packageName
                    val resIdFromR = try { R.raw::class.java.getField(name).getInt(null) } catch (_: Exception) { 0 }
                    val resId = if (resIdFromR != 0) resIdFromR else appContext.resources.getIdentifier(name, "raw", appContext.packageName)
                    if (resId != 0) {
                        finalUrl = androidx.media3.datasource.RawResourceDataSource.buildRawResourceUri(resId).toString()
                    }
                } catch (_: Exception) { }
            }

            Timber.d("PlayerService: play url=${'$'}finalUrl")
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.setMediaItem(MediaItem.fromUri(finalUrl))
            exoPlayer.prepare()
            exoPlayer.play()
        } catch (e: Exception) {
            Timber.e(e, "PlayerService: failed to play url=${'$'}url")
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

