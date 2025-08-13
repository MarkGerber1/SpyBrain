package com.example.spybrain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.spybrain.presentation.MainScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Р“Р»Р°РІРЅР°СЏ activity РїСЂРёР»РѕР¶РµРЅРёСЏ SpyBrain.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onStop() {
        super.onStop()
        // Гарантированно останавливаем звук при выходе с экрана
        try {
            val appCtx = applicationContext
            // Останов фоновой музыки
            val stopAmbient = android.content.Intent(appCtx, com.example.spybrain.service.AmbientMusicService::class.java).apply {
                action = com.example.spybrain.service.AmbientMusicService.ACTION_STOP
            }
            appCtx.startService(stopAmbient)

            // Через Hilt EntryPoint получаем сервисы и освобождаем ресурсы
            val entryPoints = dagger.hilt.android.EntryPointAccessors.fromApplication(appCtx, com.example.spybrain.di.AppEntryPoints::class.java)
            runCatching { entryPoints.playerService().stop(); entryPoints.playerService().release() }
            runCatching { entryPoints.voiceAssistantService().release() }
        } catch (_: Exception) { }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}
