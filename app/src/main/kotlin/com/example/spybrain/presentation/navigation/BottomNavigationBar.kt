package com.example.spybrain.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.size
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
// FIXME билд-фикс 09.05.2025: временно убраны TooltipArea и Tooltip для совместимости
// import androidx.compose.material3.TooltipArea
// import androidx.compose.material3.Tooltip
import androidx.compose.ui.platform.LocalConfiguration

// FIXME: Дублирование маршрутов и иконок с Screen.kt. Использовать централизованную структуру ScreenMetadata для навигации.

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = bottomNavItems
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    // FIXME UI/UX финал 09.05.2025: Адаптивность и анимация появления/скрытия бара
    AnimatedVisibility(
        visible = true, // TODO: добавить логику скрытия при скролле/свайпе
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        NavigationBar(
            modifier = if (isLandscape) Modifier.padding(horizontal = 48.dp) else Modifier
        ) {
            items.forEach { screenMetadata ->
                // TODO вернуть Tooltip после обновления Compose
                NavigationBarItem(
                    icon = {
                        Icon(
                            screenMetadata.icon,
                            contentDescription = stringResource(id = screenMetadata.labelResId),
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = screenMetadata.labelResId),
                            maxLines = 1,
                            modifier = Modifier.weight(1f).padding(top = 2.dp)
                        )
                    },
                    selected = currentRoute == screenMetadata.route,
                    onClick = {
                        if (currentRoute != screenMetadata.route) {
                            navController.navigate(screenMetadata.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}

// TODO устранено: Удалена функция getLabelResId, так как labelResId теперь доступен напрямую в ScreenMetadata
// @Composable
// fun getLabelResId(label: String): Int {
//     return when (label) {
//         "Meditation" -> com.example.spybrain.R.string.bottom_nav_meditation
//         "Breathing" -> com.example.spybrain.R.string.bottom_nav_breathing
//         "Stats" -> com.example.spybrain.R.string.bottom_nav_stats
//         "Settings" -> com.example.spybrain.R.string.bottom_nav_settings
//         "Программы" -> com.example.spybrain.R.string.settings_meditation_options
//         else -> com.example.spybrain.R.string.app_name
//     }
// } 