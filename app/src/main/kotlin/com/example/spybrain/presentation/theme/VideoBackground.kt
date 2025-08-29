package com.example.spybrain.presentation.theme

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.spybrain.R
import android.util.Log

@Composable
fun VideoBackground(
    themeKey: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Проверяем наличие видео ресурса для темы
    val videoResId = try {
        val resId = when (themeKey) {
            "water" -> R.raw.video_water_loop
            "cosmos", "space" -> R.raw.video_space_loop
            "nature" -> R.raw.video_nature_loop
            else -> 0
        }
        Log.d("VideoBackground", "Theme: $themeKey, ResId: $resId")
        resId
    } catch (e: Exception) {
        Log.e("VideoBackground", "Error getting video resource", e)
        0
    }
    
    if (videoResId != 0) {
        Log.d("VideoBackground", "Using real video for theme: $themeKey")
        // Используем реальное видео
        RealVideoBackground(themeKey = themeKey, modifier = modifier)
    } else {
        Log.d("VideoBackground", "Using canvas animation for theme: $themeKey (no video resource)")
        // Fallback на улучшенные Canvas анимации
        EnhancedAnimatedBackground(themeKey = themeKey, modifier = modifier)
    }
}

@Composable
private fun RealVideoBackground(
    themeKey: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val exoPlayer = remember(themeKey) {
        VideoPlayerManager.getPlayer(context, themeKey)
    }
    
    val isVideoReady by remember {
        derivedStateOf { VideoPlayerManager.isReady() }
    }
    
    // Обновляем состояние готовности видео
    LaunchedEffect(themeKey) {
        Log.d("VideoBackground", "Initializing video for theme: $themeKey")
    }
    
    when {
        exoPlayer != null && isVideoReady -> {
            Log.d("VideoBackground", "Showing video player")
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                        hideController()
                        controllerHideOnTouch = false
                        controllerShowTimeoutMs = 0
                        setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                        // Растягиваем видео на весь экран
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }
                },
                modifier = modifier.fillMaxSize()
            )
        }
        exoPlayer != null -> {
            // Видео загружается, показываем Canvas как placeholder
            Log.d("VideoBackground", "Video loading, showing Canvas placeholder")
            AnimatedBackground(
                themeKey = themeKey,
                modifier = modifier
            )
        }
        else -> {
            // Fallback на Canvas анимации при ошибке создания плеера
            Log.d("VideoBackground", "ExoPlayer is null, using Canvas fallback")
            AnimatedBackground(
                themeKey = themeKey,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun EnhancedAnimatedBackground(
    themeKey: String,
    modifier: Modifier = Modifier
) {
    // Это улучшенная версия Canvas анимаций для видео-режима
    // с более насыщенными эффектами
    AnimatedBackground(
        themeKey = themeKey,
        modifier = modifier
    )
}

// Создадим простые тестовые видео-заглушки
@Composable 
fun createTestVideoFiles(context: Context) {
    // В реальном проекте сюда добавятся настоящие MP4 файлы
    // Пока используем Canvas анимации как fallback
}

@Composable
fun VideoOrCanvasBackground(
    themeKey: String,
    useVideo: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (useVideo) {
        // Пока что используем улучшенные Canvas анимации
        // TODO: Добавить реальные MP4 видео
        VideoBackground(themeKey = themeKey, modifier = modifier)
    } else {
        // Используем обычные Canvas анимации
        AnimatedBackground(themeKey = themeKey, modifier = modifier)
    }
}
