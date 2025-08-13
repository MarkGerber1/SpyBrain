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

/**
 */
@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        // Короткая задержка и переход на основной экран (медитация)
        delay(600)
        navController.navigate(Screen.Meditation.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
            launchSingleTop = true
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "SpyBrain")
    }
}
