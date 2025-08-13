package com.example.spybrain.presentation.settings

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleManager {
    fun setLocale(activity: Activity, language: String) {
        setLocale(language)
        // Для применения на месте пересоздаём activity (без полноценного рестарта приложения)
        activity.recreate()
    }

    fun setLocale(language: String) {
        val tag = when (language.lowercase()) {
            "ru" -> "ru-RU"
            "en" -> "en"
            else -> language
        }
        val appLocales = LocaleListCompat.forLanguageTags(tag)
        AppCompatDelegate.setApplicationLocales(appLocales)
    }
}

