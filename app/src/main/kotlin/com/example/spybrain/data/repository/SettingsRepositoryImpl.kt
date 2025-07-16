package com.example.spybrain.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.spybrain.domain.model.Settings
import com.example.spybrain.domain.model.Theme
import com.example.spybrain.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Define Preference Keys (adjust names as needed)
private object PreferencesKeys {
    val APP_THEME = stringPreferencesKey("app_theme")
    // Add other settings keys here (e.g., language, background music)
}

/**
 * Р РµР°Р»РёР·Р°С†РёСЏ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёС… РЅР°СЃС‚СЂРѕРµРє С‡РµСЂРµР· DataStore.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences> // Assuming AppDataStore provides this
) : SettingsRepository {

    override fun getSettings(): Flow<Settings> {
        return dataStore.data.map { preferences ->
            val themeName = preferences[PreferencesKeys.APP_THEME] ?: Theme.SYSTEM.name
            val theme = try {
                Theme.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                Theme.SYSTEM // Default to system theme if saved value is invalid
            }
            Settings(theme = theme)
            // Map other preferences here
        }
    }

    override suspend fun saveSettings(settings: Settings) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = settings.theme.name
            // Save other settings here
        }
    }
}
