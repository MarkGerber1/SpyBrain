package com.example.spybrain.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spybrain.presentation.navigation.BottomNavigationBar
import com.example.spybrain.presentation.navigation.NavGraph
import com.example.spybrain.presentation.theme.DynamicBackground
import androidx.compose.runtime.CompositionLocalProvider
import com.example.spybrain.presentation.theme.LocalIconPack
import com.example.spybrain.presentation.theme.LocalThemePack
import com.example.spybrain.presentation.theme.ThemePacks
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import com.example.spybrain.service.AmbientMusicService
import com.example.spybrain.presentation.settings.SettingsViewModel

/**
 * Р“Р»Р°РІРЅС‹Р№ СЌРєСЂР°РЅ РїСЂРёР»РѕР¶РµРЅРёСЏ СЃ РЅР°РІРёРіР°С†РёРµР№.
 * @param navController РљРѕРЅС‚СЂРѕР»Р»РµСЂ РЅР°РІРёРіР°С†РёРё.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    // Автозапуск фоновой музыки при входе
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settings by settingsViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(settings.ambientEnabled, settings.ambientTrack) {
        // Больше не автозапускаем музыку. Только гарантируем стоп, если выключено.
        if (!settings.ambientEnabled || settings.ambientTrack.isEmpty()) {
            runCatching {
                val intent = Intent(context, AmbientMusicService::class.java).apply { action = AmbientMusicService.ACTION_STOP }
                context.startService(intent)
            }
        }
    }

    val themePack = ThemePacks.themePackFor(settings.theme)
    val iconPack = ThemePacks.iconPackFor(settings.theme)

    CompositionLocalProvider(LocalThemePack provides themePack, LocalIconPack provides iconPack) {
    DynamicBackground {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                NavGraph(navController = navController)
            }
        }
    }
    }
}

