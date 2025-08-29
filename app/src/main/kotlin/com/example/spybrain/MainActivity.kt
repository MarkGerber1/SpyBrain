package com.example.spybrain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.spybrain.presentation.MainScreen
import com.example.spybrain.presentation.theme.SpyBrainTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.spybrain.presentation.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import com.example.spybrain.presentation.navigation.BottomNavigationBar
import com.example.spybrain.presentation.navigation.Screen

/**
 * Р“Р»Р°РІРЅР°СЏ activity РїСЂРёР»РѕР¶РµРЅРёСЏ SpyBrain.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onPause() {
        super.onPause()
        // Останавливаем ambient музыку при сворачивании приложения
        try {
            val intent = android.content.Intent(this, com.example.spybrain.service.AmbientMusicService::class.java).apply {
                action = com.example.spybrain.service.AmbientMusicService.ACTION_STOP
            }
            startService(intent)
        } catch (_: Exception) { }
    }

    override fun onResume() {
        super.onResume()
        // Восстанавливаем ambient музыку при возврате в приложение (если была включена)
        try {
            val appCtx = applicationContext
            val entryPoints = dagger.hilt.android.EntryPointAccessors.fromApplication(appCtx, com.example.spybrain.di.AppEntryPoints::class.java)
            val dataStore = entryPoints.settingsDataStore()
            if (dataStore.getAmbientEnabled()) {
                val trackId = dataStore.getAmbientTrack()
                val intent = android.content.Intent(this, com.example.spybrain.service.AmbientMusicService::class.java).apply {
                    action = com.example.spybrain.service.AmbientMusicService.ACTION_PLAY
                    putExtra(com.example.spybrain.service.AmbientMusicService.EXTRA_TRACK_ID, trackId)
                }
                startService(intent)
            }
        } catch (_: Exception) { }
    }

    override fun onStop() {
        super.onStop()
        // Останавливаем музыку при уходе из приложения
        try {
            val appCtx = applicationContext
            val entryPoints = dagger.hilt.android.EntryPointAccessors.fromApplication(appCtx, com.example.spybrain.di.AppEntryPoints::class.java)
            runCatching { 
                entryPoints.playerService().stop()
            }
        } catch (_: Exception) { }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Освобождаем видео плеер для предотвращения утечек памяти
        com.example.spybrain.presentation.theme.VideoPlayerManager.releasePlayer()
        
        // Гарантированно останавливаем все сервисы при закрытии приложения
        try {
            val appCtx = applicationContext
            val entryPoints = dagger.hilt.android.EntryPointAccessors.fromApplication(appCtx, com.example.spybrain.di.AppEntryPoints::class.java)
            runCatching { 
                entryPoints.playerService().stop()
            }
        } catch (_: Exception) { }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val uiState = settingsViewModel.uiState.collectAsState()
            // Прокидываем ThemePack через CompositionLocal, чтобы DynamicBackground видел Lottie
            val themePack = com.example.spybrain.presentation.theme.ThemePacks.themePackFor(uiState.value.theme)
            androidx.compose.runtime.CompositionLocalProvider(
                com.example.spybrain.presentation.theme.LocalThemePack provides themePack,
                com.example.spybrain.presentation.theme.LocalIconPack provides themePack.icons
            ) {
            SpyBrainTheme(themeKey = uiState.value.theme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.ui.graphics.Color.Transparent
                ) {
                    // Оборачиваем все приложение в DynamicBackground для живых фонов на всех экранах
                    com.example.spybrain.presentation.theme.DynamicBackground {
                        val navController = androidx.navigation.compose.rememberNavController()
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        val showBottomBar = currentRoute != com.example.spybrain.presentation.navigation.Screen.Splash.route

                        Scaffold(
                            containerColor = androidx.compose.ui.graphics.Color.Transparent,
                            bottomBar = {
                                if (showBottomBar) {
                                    BottomNavigationBar(navController)
                                }
                            }
                        ) { padding ->
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)) {
                                com.example.spybrain.presentation.navigation.NavGraph(navController = navController)
                            }
                        }
                    }
                }
            }
            }
        }
    }
}
