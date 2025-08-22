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
        // Убираем автозапуск музыки - музыка теперь запускается только по действию пользователя
        runCatching {
            val entryPoints = dagger.hilt.android.EntryPointAccessors.fromApplication(context.applicationContext, com.example.spybrain.di.AppEntryPoints::class.java)
            // Всегда останавливаем музыку при запуске приложения
            val stopIntent = Intent(context, AmbientMusicService::class.java).apply {
                action = AmbientMusicService.ACTION_STOP
            }
            context.startService(stopIntent)
        }
        runCatching { VibrationUtil.shortVibration(context) }
        // Больше не озвучиваем интро на сплэше, только музыка и вибрация

        // Короткая задержка и переход: если имя не задано — на онбординг
        delay(1200)
        val hasName = try {
            val entryPoints = dagger.hilt.android.EntryPointAccessors.fromApplication(context.applicationContext, com.example.spybrain.di.AppEntryPoints::class.java)
            entryPoints.settingsDataStore().getUserName().isNotBlank()
        } catch (_: Exception) { false }
        val target = if (hasName) Screen.Main.route else Screen.Onboarding.route
        navController.navigate(target) {
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
