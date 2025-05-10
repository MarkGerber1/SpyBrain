package com.example.spybrain.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spybrain.presentation.navigation.BottomNavigationBar
import com.example.spybrain.presentation.navigation.NavGraph
import com.example.spybrain.presentation.components.SmartWelcomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // FIXME билд-фикс 09.05.2025: replace delegated var with mutableStateOf
            val showWelcome = remember { mutableStateOf(true) }
            if (showWelcome.value) {
                SmartWelcomeScreen(
                    isOffline = false, // TODO: заменить на реальную проверку сети
                    onQuickAction = { action ->
                        showWelcome.value = false
                        when (action) {
                            "breathing" -> navController.navigate("breathing")
                            "meditation" -> navController.navigate("meditation")
                        }
                    }
                )
            } else {
                NavGraph(navController = navController)
            }
        }
    }
} 