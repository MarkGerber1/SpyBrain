package com.example.spybrain.presentation.theme

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.spybrain.R

/**
 * Синглтон для управления одним экземпляром ExoPlayer
 * для предотвращения крашей от множественных плееров
 */
object VideoPlayerManager {
    private var exoPlayer: ExoPlayer? = null
    private var currentTheme: String? = null
    private var isPlayerReady = false
    
    fun getPlayer(context: Context, themeKey: String): ExoPlayer? {
        try {
            // Если тема изменилась или плеер не создан - пересоздаем
            if (exoPlayer == null || currentTheme != themeKey) {
                Log.d("VideoPlayerManager", "Creating/recreating player for theme: $themeKey")
                
                // Освобождаем предыдущий плеер
                exoPlayer?.release()
                isPlayerReady = false
                
                // Получаем ресурс для темы
                val videoResId = when (themeKey) {
                    "water" -> R.raw.video_water_loop
                    "cosmos", "space" -> R.raw.video_space_loop
                    "nature" -> R.raw.video_nature_loop
                    else -> 0
                }
                
                if (videoResId == 0) {
                    Log.w("VideoPlayerManager", "No video resource for theme: $themeKey")
                    return null
                }
                
                // Создаем новый плеер
                val player = ExoPlayer.Builder(context).build()
                val uri = Uri.parse("android.resource://${context.packageName}/$videoResId")
                val mediaItem = MediaItem.fromUri(uri)
                
                player.apply {
                    setMediaItem(mediaItem)
                    prepare()
                    playWhenReady = true
                    repeatMode = Player.REPEAT_MODE_ONE
                    volume = 0f // Без звука
                    
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            isPlayerReady = playbackState == Player.STATE_READY
                            Log.d("VideoPlayerManager", "Player state changed: $playbackState, ready: $isPlayerReady")
                        }
                        
                        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                            Log.e("VideoPlayerManager", "Player error: ${error.message}", error)
                            isPlayerReady = false
                        }
                    })
                }
                
                exoPlayer = player
                currentTheme = themeKey
                Log.d("VideoPlayerManager", "✅ Player created for $themeKey")
            }
            
            return exoPlayer
        } catch (e: Exception) {
            Log.e("VideoPlayerManager", "Failed to create player", e)
            return null
        }
    }
    
    fun isReady(): Boolean = isPlayerReady
    
    fun releasePlayer() {
        Log.d("VideoPlayerManager", "Releasing player")
        exoPlayer?.release()
        exoPlayer = null
        currentTheme = null
        isPlayerReady = false
    }
    
    fun getCurrentTheme(): String? = currentTheme
}
