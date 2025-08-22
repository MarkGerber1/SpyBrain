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
 * Главный экран приложения с навигацией.
 * @param navController Контроллер навигации.
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
            // Показываем NavGraph напрямую, без welcome screen
            NavGraph(navController = navController)
        }
    }
}

@Composable
fun MainScreen() {
    mainScreen()
}

// Если компонента нет, добавить:
// @Composable
// fun MainScreen() { /* TODO: Реализовать */ }
