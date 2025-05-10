package com.example.spybrain.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import com.example.spybrain.presentation.settings.SettingsScreen
import com.example.spybrain.presentation.meditation.MeditationScreen
import com.example.spybrain.presentation.meditation.MeditationViewModel
import com.example.spybrain.presentation.breathing.BreathingScreen
import com.example.spybrain.presentation.breathing.patternbuilder.BreathingPatternBuilderScreen
import com.example.spybrain.presentation.stats.StatsScreen
import com.example.spybrain.presentation.stats.StatsViewModel
import com.example.spybrain.presentation.splash.SplashScreen
import com.example.spybrain.presentation.settings.SettingsViewModel
import com.example.spybrain.presentation.profile.ProfileViewModel
import com.example.spybrain.presentation.meditation.MeditationLibraryScreen
import com.example.spybrain.presentation.profile.ProfileScreen
import com.example.spybrain.presentation.biosync.BioSyncScreen
import com.example.spybrain.presentation.breathing.patternbuilder.EditCustomBreathingPatternScreen
import androidx.navigation.compose.NavHost            // FIXME билд-фикс 09.05.2025
import androidx.navigation.compose.composable        // FIXME билд-фикс 09.05.2025
import androidx.navigation.compose.navArgument       // FIXME билд-фикс 09.05.2025
import androidx.navigation.compose.navType          // FIXME билд-фикс 09.05.2025
import com.example.spybrain.presentation.stats.StatsScreen // FIXME билд-фикс 09.05.2025

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.MeditationLibrary.route) {
            MeditationLibraryScreen()
        }
        composable(Screen.Meditation.route) {
            MeditationScreen()
        }
        composable(
            route = Screen.Breathing.route,
            content = { BreathingScreen(navController) }
        )
        composable(Screen.PatternBuilder.route) {
            BreathingPatternBuilderScreen()
        }
        composable(
            route = "${Screen.Pattern.route}/{patternId}",
            arguments = listOf(
                androidx.navigation.compose.navArgument("patternId") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) {
            PatternScreen(navController, it.arguments?.getString("patternId"))
        }
        composable(Screen.CustomPatterns.route) {
            BreathingPatternBuilderScreen()
        }
        composable(Screen.Stats.route) {
            StatsScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        composable(Screen.BioSync.route) {
            BioSyncScreen()
        }
    }
} 