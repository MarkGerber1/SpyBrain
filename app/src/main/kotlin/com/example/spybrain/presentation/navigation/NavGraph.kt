package com.example.spybrain.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.spybrain.R
import com.example.spybrain.presentation.breathing.breathingScreen
import com.example.spybrain.presentation.breathing.patternbuilder.breathingPatternBuilderScreen
import com.example.spybrain.presentation.breathing.patternbuilder.editCustomBreathingPatternScreen
import com.example.spybrain.presentation.biosync.bioSyncScreen
import com.example.spybrain.presentation.meditation.meditationLibraryScreen
import com.example.spybrain.presentation.meditation.MeditationScreen
import com.example.spybrain.presentation.profile.ProfileScreen
import com.example.spybrain.presentation.reminders.HeartRateScreen
import com.example.spybrain.presentation.settings.SettingsScreen
import com.example.spybrain.presentation.stats.StatsScreen
import com.example.spybrain.presentation.splash.SplashScreen
import com.example.spybrain.presentation.achievements.achievementsScreen
import com.example.spybrain.presentation.MainScreen
import com.example.spybrain.presentation.onboarding.OnboardingScreen

// Alias functions for navigation
@Composable
fun BreathingScreenAlias(navController: NavHostController) = breathingScreen(navController)

@Composable
fun BreathingPatternBuilderScreenAlias(navController: NavHostController) = breathingPatternBuilderScreen(navController as androidx.navigation.NavController, onBackPressed = { navController.popBackStack() })

@Composable
fun EditCustomBreathingPatternScreenAlias(navController: NavHostController) = editCustomBreathingPatternScreen(navController as androidx.navigation.NavController, patternId = "0")

@Composable
fun BioSyncScreenAlias() = bioSyncScreen()

@Composable
fun MeditationLibraryScreenAlias() = meditationLibraryScreen()

@Composable
fun MeditationScreenAlias() = MeditationScreen()

@Composable
fun AchievementsScreenAlias() = achievementsScreen()

/**
 * @param navController Контроллер навигации.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Main.route) {
            MainScreen(navController)
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinished = {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }

        composable(Screen.Meditation.route) {
            MeditationScreenAlias()
        }

        composable(Screen.Breathing.route) {
            BreathingScreenAlias(navController)
        }

        composable(Screen.Stats.route) {
            StatsScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        composable(Screen.MeditationLibrary.route) {
            MeditationLibraryScreenAlias()
        }
        composable(Screen.PatternBuilder.route) {
            BreathingPatternBuilderScreenAlias(navController)
        }
        composable(Screen.EditCustomPattern.route) {
            EditCustomBreathingPatternScreenAlias(navController)
        }
        composable(Screen.BioSync.route) {
            BioSyncScreenAlias()
        }
        composable(Screen.HeartRate.route) {
            HeartRateScreen(navController)
        }
        composable(Screen.Achievements.route) {
            AchievementsScreenAlias()
        }
    }
}
