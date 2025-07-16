package com.example.spybrain.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spybrain.presentation.navigation.BottomNavigationBar
import com.example.spybrain.presentation.navigation.NavGraph

/**
 * Р“Р»Р°РІРЅС‹Р№ СЌРєСЂР°РЅ РїСЂРёР»РѕР¶РµРЅРёСЏ СЃ РЅР°РІРёРіР°С†РёРµР№.
 * @param navController РљРѕРЅС‚СЂРѕР»Р»РµСЂ РЅР°РІРёРіР°С†РёРё.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mainScreen(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // РџРѕРєР°Р·С‹РІР°РµРј NavGraph РЅР°РїСЂСЏРјСѓСЋ, Р±РµР· welcome screen
            NavGraph(navController = navController)
        }
    }
}

@Composable
fun MainScreen() {
    // TODO: Реализовать основной экран
}

// Если компонента нет, добавить:
// @Composable
// fun MainScreen() { /* TODO: Реализовать */ }
