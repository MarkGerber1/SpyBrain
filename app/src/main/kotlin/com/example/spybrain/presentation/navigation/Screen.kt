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
import com.example.spybrain.R // РРјРїРѕСЂС‚ РґР»СЏ РґРѕСЃС‚СѓРїР° Рє СЃС‚СЂРѕРєРѕРІС‹Рј СЂРµСЃСѓСЂСЃР°Рј
import androidx.compose.material.icons.filled.Spa // TODO: Р”РѕР±Р°РІРёС‚СЊ Р±РѕР»РµРµ РїРѕРґС…РѕРґСЏС‰РёРµ РёРєРѕРЅРєРё РґР»СЏ РІСЃРµС… СЌРєСЂР°РЅРѕРІ
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.SelfImprovement // TODO СЂРµР°Р»РёР·РѕРІР°РЅРѕ: РРјРїРѕСЂС‚ РЅРѕРІРѕР№ РёРєРѕРЅРєРё РґР»СЏ РјРµРґРёС‚Р°С†РёРё
import androidx.compose.material.icons.filled.Air // TODO СЂРµР°Р»РёР·РѕРІР°РЅРѕ: РРјРїРѕСЂС‚ РЅРѕРІРѕР№ РёРєРѕРЅРєРё РґР»СЏ РґС‹С…Р°РЅРёСЏ
import androidx.compose.material.icons.filled.BarChart 
import androidx.compose.material.icons.filled.Notifications

/**
 * @property route РњР°СЂС€СЂСѓС‚ РЅР°РІРёРіР°С†РёРё.
 */
sealed class Screen(
    val route: String
) {
    /** Р­РєСЂР°РЅ РїСЂРёРІРµС‚СЃС‚РІРёСЏ. */
    object Splash : Screen("splash")
    /** Р­РєСЂР°РЅ РјРµРґРёС‚Р°С†РёРё. */
    object Meditation : Screen("meditation")
    /** Р­РєСЂР°РЅ РґС‹С…Р°РЅРёСЏ. */
    object Breathing : Screen("breathing")
    /** Р­РєСЂР°РЅ СЃС‚Р°С‚РёСЃС‚РёРєРё. */
    object Stats : Screen("stats")
    /** Р­РєСЂР°РЅ РЅР°СЃС‚РѕРµРє. */
    object Settings : Screen("settings")
    /** Р­РєСЂР°РЅ РјРµРґРёС‚Р°С†РёРё. */
    object Profile : Screen("profile")
    /** Р­РєСЂР°РЅ РєРѕРЅСЃС‚СЂСѓРєС‚РѕСЂР° РїР°С‚С‚РµСЂРЅРѕРІ. */
    object PatternBuilder : Screen("pattern_builder")
    /** Р­РєСЂР°РЅ РїРѕР»СЊР·РѕРІР°С‚РµР»СЃРєРёС… РїР°С‚С‚РµСЂРЅРѕРІ. */
    object CustomPatterns : Screen("custom_patterns")
    /** Р­РєСЂР°РЅ СЂРµРґР°РєС‚РёСЂРѕРІР°РЅРёСЏ РїР°С‚С‚РµСЂРЅР°. */
    object EditCustomPattern : Screen("edit_custom_pattern/{patternId}")
    /** Р­РєСЂР°РЅ Р±РёР±Р»РёРѕС‚РµРєРё РјРµРґРёС‚Р°С†РёР№. */
    object MeditationLibrary : Screen("meditation_library")
    /** Р­РєСЂР°РЅ РґРѕСЃС‚РёР¶РµРЅРёР№. */
    object Achievements : Screen("achievements")
    /** Р­РєСЂР°РЅ Р±РёРѕСЃРёРЅС…СЂРѕРЅРёР·Р°С†РёРё. */
    object BioSync : Screen("biosync")
    /** Р­РєСЂР°РЅ РїСѓР»СЊСЃР°. */
    object HeartRate : Screen("heart_rate")
}

/**
 */
data class ScreenMetadata(
    val route: String,
    val icon: ImageVector,
    val labelResId: Int
)

/**
 * РЎРїРёСЃРѕРє РјРµС‚Р°РґР°РЅРЅС‹С… РґР»СЏ СЌРєСЂР°РЅРѕРІ РЅРёР¶РЅРµР№ РЅР°РІРёРіР°С†РёРё.
 */
val bottomNavItems = listOf(
    // TODO СЂРµР°Р»РёР·РѕРІР°РЅРѕ: РџСЂРѕРІРµСЂРµРЅС‹ Рё РѕР±РЅРѕРІР»РµРЅС‹ РёРєРѕРЅРєРё РЅР° СЃС‚Р°РЅРґР°СЂС‚РЅС‹Рµ. РџСЂРё РЅРµРѕР±С…РѕРґРёРјРѕСЃС‚Рё РјРѕР¶РЅРѕ Р·Р°РјРµРЅРёС‚СЊ РЅР° РєР°СЃС‚РѕРјРЅС‹Рµ Р°СЃСЃРµС‚С‹.
    ScreenMetadata(
        Screen.Meditation.route,
        Icons.Default.SelfImprovement,
        R.string.bottom_nav_meditation
    ), // TODO СЂРµР°Р»РёР·РѕРІР°РЅРѕ: Р—Р°РјРµРЅРµРЅР° РёРєРѕРЅРєР° РјРµРґРёС‚Р°С†РёРё
    ScreenMetadata(
        Screen.Breathing.route,
        Icons.Default.Air,
        R.string.bottom_nav_breathing
    ), // TODO СЂРµР°Р»РёР·РѕРІР°РЅРѕ: Р—Р°РјРµРЅРµРЅР° РёРєРѕРЅРєР° РґС‹С…Р°РЅРёСЏ
    ScreenMetadata(
        Screen.Stats.route,
        Icons.Default.Timeline,
        R.string.bottom_nav_stats
    ), // NOTE СЂРµР°Р»РёР·РѕРІР°РЅРѕ РїРѕ Р°СѓРґРёС‚Сѓ: РћСЃС‚Р°РІР»РµРЅР° РїРѕРґС…РѕРґСЏС‰Р°СЏ РёРєРѕРЅРєР° СЃС‚Р°С‚РёСЃС‚РёРєРё
    ScreenMetadata(
        Screen.HeartRate.route,
        Icons.Default.Favorite,
        R.string.bottom_nav_heart_rate
    ),
    ScreenMetadata(
        Screen.Settings.route,
        Icons.Default.Settings,
        R.string.bottom_nav_settings
    ) // NOTE СЂРµР°Р»РёР·РѕРІР°РЅРѕ РїРѕ Р°СѓРґРёС‚Сѓ: РћСЃС‚Р°РІР»РµРЅР° РїРѕРґС…РѕРґСЏС‰Р°СЏ РёРєРѕРЅРєР° РЅР°СЃС‚СЂРѕРµРє
)

// FIXME УСТРАНЕНО: Дублирование иконок и маршрутов. Вынести метаданные экранов в централизованную структуру (ScreenMetadata).

// TODO: Реализовано: Проверены и обновлены иконки на стандартные.
// При необходимости можно заменить на кастомные ассеты.
