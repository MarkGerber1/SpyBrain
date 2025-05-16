package com.example.spybrain.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.spybrain.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    // Создаем фиксированный список элементов навигации для гарантированного отображения
    val items = listOf(
        Triple(Screen.Meditation.route, Icons.Default.SelfImprovement, R.string.bottom_nav_meditation),
        Triple(Screen.Breathing.route, Icons.Default.Air, R.string.bottom_nav_breathing),
        Triple(Screen.Stats.route, Icons.Default.Timeline, R.string.bottom_nav_stats),
        Triple(Screen.Reminders.route, Icons.Default.Notifications, R.string.bottom_nav_reminders),
        Triple(Screen.Settings.route, Icons.Default.Settings, R.string.bottom_nav_settings)
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar {
        items.forEach { (route, icon, labelResId) ->
            val selected = currentDestination?.hierarchy?.any { it.route == route } == true
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(labelResId),
                        modifier = Modifier.size(24.dp)
                    ) 
                },
                label = { Text(stringResource(labelResId)) },
                selected = selected,
                onClick = {
                    navController.navigate(route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
} 