package com.example.spybrain.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.spybrain.R
import com.example.spybrain.presentation.breathing.BreathingScreen
import com.example.spybrain.presentation.breathing.patternbuilder.BreathingPatternBuilderScreen
import com.example.spybrain.presentation.breathing.patternbuilder.EditCustomBreathingPatternScreen
import com.example.spybrain.presentation.biosync.BioSyncScreen
import com.example.spybrain.presentation.meditation.MeditationLibraryScreen
import com.example.spybrain.presentation.meditation.MeditationScreen
import com.example.spybrain.presentation.profile.ProfileScreen
import com.example.spybrain.presentation.reminders.HeartRateScreen
import com.example.spybrain.presentation.settings.SettingsScreen
import com.example.spybrain.presentation.stats.StatsScreen
import com.example.spybrain.presentation.splash.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Meditation.route) {

        composable(Screen.Splash.route) { 
            SplashScreen(navController) 
        }

        composable(Screen.Meditation.route) {
            MeditationScreen()
        }

        composable(Screen.Breathing.route) {
            BreathingScreen(navController)
        }

        composable(Screen.Stats.route) {
            StatsScreen()
        }
        
        composable(Screen.HeartRate.route) {
            HeartRateScreen(navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }

        composable(Screen.MeditationLibrary.route) {
            MeditationLibraryScreen()
        }
        
        composable(Screen.PatternBuilder.route) {
            BreathingPatternBuilderScreen(navController)
        }

        composable(
            route = "${Screen.EditCustomPattern.route}/{patternId}",
            arguments = listOf(navArgument("patternId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditCustomBreathingPatternScreen(
                navController = navController,
                patternId = backStackEntry.arguments?.getString("patternId") ?: ""
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        composable(Screen.BioSync.route) {
            BioSyncScreen()
        }
        
        composable(Screen.Achievements.route) {
            // TODO: Добавить экран достижений
            com.example.spybrain.presentation.achievements.AchievementsScreen()
        }
    }
} 