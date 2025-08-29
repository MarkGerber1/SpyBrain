package com.example.spybrain.presentation.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.spybrain.R

data class IconPack(
    val play: ImageVector,
    val pause: ImageVector,
    val stop: ImageVector,
    val settings: ImageVector,
    val back: ImageVector,
    // навигация
    val navMeditation: ImageVector,
    val navBreathing: ImageVector,
    val navStats: ImageVector,
    val navHeartRate: ImageVector,
    val navSettings: ImageVector,
)

data class ThemePack(
    val name: String,
    val colorScheme: ColorScheme,
    val backgroundLottie: Int?,
    val icons: IconPack
)

val LocalThemePack = staticCompositionLocalOf {
    // Будет переопределено выше по дереву
    ThemePack(
        name = "Nature",
        colorScheme = colorSchemeForThemeKey("nature", false),
        backgroundLottie = R.raw.lottie_meditation,
        icons = IconPack(
            play = Icons.Rounded.PlayArrow,
            pause = Icons.Rounded.Pause,
            stop = Icons.Rounded.Stop,
            settings = Icons.Filled.Settings,
            back = Icons.Rounded.ArrowBack,
            navMeditation = Icons.Filled.SelfImprovement,
            navBreathing = Icons.Filled.Air,
            navStats = Icons.Filled.Timeline,
            navHeartRate = Icons.Filled.Favorite,
            navSettings = Icons.Filled.Settings
        )
    )
}

val LocalIconPack = staticCompositionLocalOf {
    IconPack(
        play = Icons.Rounded.PlayArrow,
        pause = Icons.Rounded.Pause,
        stop = Icons.Rounded.Stop,
        settings = Icons.Filled.Settings,
        back = Icons.Rounded.ArrowBack,
        navMeditation = Icons.Filled.SelfImprovement,
        navBreathing = Icons.Filled.Air,
        navStats = Icons.Filled.Timeline,
        navHeartRate = Icons.Filled.Favorite,
        navSettings = Icons.Filled.Settings
    )
}

object ThemePacks {
    private fun defaultIconPack(): IconPack = IconPack(
        play = Icons.Rounded.PlayArrow,
        pause = Icons.Rounded.Pause,
        stop = Icons.Rounded.Stop,
        settings = Icons.Filled.Settings,
        back = Icons.Rounded.ArrowBack,
        navMeditation = Icons.Filled.SelfImprovement,
        navBreathing = Icons.Filled.Air,
        navStats = Icons.Filled.Timeline,
        navHeartRate = Icons.Filled.Favorite,
        navSettings = Icons.Filled.Settings
    )

    fun themePackFor(themeKey: String, dark: Boolean = false): ThemePack = when (themeKey) {
        "water" -> ThemePack(
            name = "Water",
            colorScheme = colorSchemeForThemeKey("water", dark),
            backgroundLottie = R.raw.lottie_water_vivid,
            icons = defaultIconPack()
        )
        "space" -> ThemePack(
            name = "Cosmos",
            colorScheme = colorSchemeForThemeKey("space", dark),
            backgroundLottie = R.raw.lottie_space_vivid,
            icons = defaultIconPack()
        )
        else -> ThemePack(
            name = "Nature",
            colorScheme = colorSchemeForThemeKey("nature", dark),
            backgroundLottie = R.raw.lottie_clouds,
            icons = defaultIconPack()
        )
    }
}

// Локальная копия маппинга цветовых схем по ключу темы (аналогично AppTheme)
internal fun colorSchemeForThemeKey(themeKey: String, dark: Boolean): androidx.compose.material3.ColorScheme {
    val base = if (dark) darkColorScheme() else lightColorScheme()
    return when (themeKey) {
        "water" -> base.copy(
            primary = Blue80,
            secondary = BlueGrey40,
            tertiary = Blue40
        )
        "space" -> base.copy(
            primary = Purple80,
            secondary = PurpleGrey80,
            tertiary = Pink80
        )
        "air" -> base.copy(
            primary = Teal40,
            secondary = BlueGrey40,
            tertiary = Teal80
        )
        else -> base.copy(
            primary = Green40,
            secondary = Green80,
            tertiary = Teal40
        )
    }
}


