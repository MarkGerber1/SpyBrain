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
    override fun onStop() {
        super.onStop()
        // Не глушим ambient на уход в фон, чтобы музыка могла играть в фоне.
        try {
            val appCtx = applicationContext
            val entryPoints = dagger.hilt.android.EntryPointAccessors.fromApplication(appCtx, com.example.spybrain.di.AppEntryPoints::class.java)
            runCatching { entryPoints.playerService().pause() }
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
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = androidx.navigation.compose.rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val showBottomBar = currentRoute != com.example.spybrain.presentation.navigation.Screen.Splash.route

                    Scaffold(
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
