package com.example.spybrain.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Camera
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.spybrain.R // Импорт для доступа к строковым ресурсам
import androidx.compose.material.icons.filled.Spa // TODO: Добавить более подходящие иконки для всех экранов
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.SelfImprovement // TODO реализовано: Импорт новой иконки для медитации
import androidx.compose.material.icons.filled.Air // TODO реализовано: Импорт новой иконки для дыхания
import androidx.compose.material.icons.filled.BarChart // TODO: Можно рассмотреть для статистики, но Timeline тоже подходит
import androidx.compose.material.icons.filled.Notifications

// TODO реализовано: Создание централизованной структуры ScreenMetadata
data class ScreenMetadata(
    val route: String,
    val icon: ImageVector,
    val labelResId: Int
)

// TODO реализовано: Список метаданных для экранов нижней навигации
val bottomNavItems = listOf(
    // TODO реализовано: Проверены и обновлены иконки на стандартные. При необходимости можно заменить на кастомные ассеты.
    ScreenMetadata(Screen.Meditation.route, Icons.Default.SelfImprovement, R.string.bottom_nav_meditation), // TODO реализовано: Заменена иконка медитации
    ScreenMetadata(Screen.Breathing.route, Icons.Default.Air, R.string.bottom_nav_breathing), // TODO реализовано: Заменена иконка дыхания
    ScreenMetadata(Screen.Stats.route, Icons.Default.Timeline, R.string.bottom_nav_stats), // NOTE реализовано по аудиту: Оставлена подходящая иконка статистики
    ScreenMetadata(Screen.Reminders.route, Icons.Default.Notifications, R.string.bottom_nav_reminders), // Добавлен экран напоминаний
    ScreenMetadata(Screen.Settings.route, Icons.Default.Settings, R.string.bottom_nav_settings) // NOTE реализовано по аудиту: Оставлена подходящая иконка настроек
)

// FIXME устранено: Дублирование иконок и маршрутов. Вынести метаданные экранов в централизованную структуру (ScreenMetadata) и синхронизировать с BottomNavigationBar. Для каждого экрана использовать уникальную иконку.

sealed class Screen(
    val route: String
) {
    // NOTE реализовано по аудиту: Сохранены маршруты в sealed class для использования в навигации
    object Splash : Screen("splash")
    object Meditation : Screen("meditation")
    object Breathing : Screen("breathing")
    object Stats : Screen("stats")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object PatternBuilder : Screen("pattern_builder")
    object CustomPatterns : Screen("custom_patterns")
    object EditCustomPattern : Screen("edit_custom_pattern/{patternId}")
    object MeditationLibrary : Screen("meditation_library")
    object Achievements : Screen("achievements")
    object BioSync : Screen("biosync")
    object Reminders : Screen("reminders")
} 