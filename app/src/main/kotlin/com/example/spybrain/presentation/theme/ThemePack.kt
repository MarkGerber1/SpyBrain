package com.example.spybrain.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.spybrain.R

data class IconPack(
	val themeIconRes: Int
)

data class ThemePack(
	val backgroundImageRes: Int,
	val accentIconPack: IconPack
)

val LocalIconPack = staticCompositionLocalOf { IconPack(themeIconRes = R.drawable.ic_nature) }
val LocalThemePack = staticCompositionLocalOf {
	ThemePack(
		backgroundImageRes = R.drawable.bg_nature,
		accentIconPack = IconPack(R.drawable.ic_nature)
	)
}

object ThemePacks {
	fun iconPackFor(themeKey: String): IconPack = when (themeKey) {
		"water" -> IconPack(R.drawable.ic_water)
		"space" -> IconPack(R.drawable.ic_space)
		"nature" -> IconPack(R.drawable.ic_nature)
		"air" -> IconPack(R.drawable.ic_air)
		else -> IconPack(R.drawable.ic_nature)
	}

	fun themePackFor(themeKey: String): ThemePack = when (themeKey) {
		"water" -> ThemePack(
			backgroundImageRes = R.drawable.bg_water,
			accentIconPack = iconPackFor("water")
		)
		"space" -> ThemePack(
			backgroundImageRes = R.drawable.bg_space,
			accentIconPack = iconPackFor("space")
		)
		"air" -> ThemePack(
			backgroundImageRes = R.drawable.bg_air,
			accentIconPack = iconPackFor("air")
		)
		else -> ThemePack(
			backgroundImageRes = R.drawable.bg_nature,
			accentIconPack = iconPackFor("nature")
		)
	}
}


