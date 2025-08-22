package com.example.spybrain.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import com.example.spybrain.data.datastore.SettingsDataStore
import com.example.spybrain.presentation.navigation.Screen

/**
 */
@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    val settings = SettingsDataStore(context)
    val onboarded by settings.onboardedFlow.collectAsState(initial = false)

    LaunchedEffect(onboarded) {
        delay(800)
        if (onboarded) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "SpyBrain")
    }
}
