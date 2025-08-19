package com.example.spybrain.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.spybrain.presentation.navigation.Screen
import kotlinx.coroutines.delay
import com.example.spybrain.presentation.theme.DynamicBackground
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import com.example.spybrain.service.AmbientMusicService
import com.example.spybrain.util.VibrationUtil
import dagger.hilt.android.EntryPointAccessors
import com.example.spybrain.di.AppEntryPoints

/**
 */
@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // На всякий случай глушим любые возможные очереди TTS при старте
        runCatching {
            val entryPoints = dagger.hilt.android.EntryPointAccessors.fromApplication(context.applicationContext, com.example.spybrain.di.AppEntryPoints::class.java)
            entryPoints.voiceAssistantService().stopGuidance()
            entryPoints.voiceAssistantService().stop()
        }

        // Стартуем приветственную анимацию/звук/вибрацию
        // Проверяем настройки пользователя: запускать музыку только если включено в настройках
        runCatching {
            val entryPoints = dagger.hilt.android.EntryPointAccessors.fromApplication(context.applicationContext, com.example.spybrain.di.AppEntryPoints::class.java)
            val settings = entryPoints.settingsDataStore()
            if (settings.getAmbientEnabled() && settings.getAmbientTrack().isNotEmpty()) {
                val playIntent = Intent(context, AmbientMusicService::class.java).apply {
                    action = AmbientMusicService.ACTION_PLAY
                    putExtra(AmbientMusicService.EXTRA_TRACK_ID, settings.getAmbientTrack())
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(playIntent)
                } else {
                    context.startService(playIntent)
                }
            }
            // Если ambient выключен — убедимся, что сервис остановлен
            if (!settings.getAmbientEnabled()) {
                val stopIntent = Intent(context, AmbientMusicService::class.java).apply {
                    action = AmbientMusicService.ACTION_STOP
                }
                context.startService(stopIntent)
            }
        }
        runCatching { VibrationUtil.shortVibration(context) }
        // Больше не озвучиваем интро на сплэше, только музыка и вибрация

        // Короткая задержка и переход на главный экран с нижней навигацией
        delay(1200)
        navController.navigate(Screen.Main.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    DynamicBackground(lottieKeyOverride = "lottie_meditation") {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
            Text(
                text = "SpyBrain",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 36.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
