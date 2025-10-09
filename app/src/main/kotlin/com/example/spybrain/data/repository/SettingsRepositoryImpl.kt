package com.example.spybrain.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.spybrain.domain.model.Settings
import com.example.spybrain.domain.model.Theme
import com.example.spybrain.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

// Define Preference Keys
private object PreferencesKeys {
    val APP_THEME = stringPreferencesKey("app_theme")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_BIRTH_DATE = stringPreferencesKey("user_birth_date")
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    val SMART_AMBIENT_ENABLED = booleanPreferencesKey("smart_ambient_enabled")
    val SLEEP_TIMER_MINUTES = intPreferencesKey("sleep_timer_minutes")
}

/**
 * Реализация репозитория пользовательских настроек через DataStore.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
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
        }
    }

    override suspend fun saveSettings(settings: Settings) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = settings.theme.name
        }
    }

    override suspend fun getUserName(): String {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_NAME] ?: ""
        }.firstOrNull() ?: ""
    }

    override suspend fun setUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    override suspend fun getUserBirthDate(): LocalDate? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_BIRTH_DATE]
        }.firstOrNull()?.let { dateString ->
            try {
                LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun setUserBirthDate(birthDate: LocalDate) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_BIRTH_DATE] = birthDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }

    override suspend fun isOnboardingCompleted(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }.firstOrNull() ?: false
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    override suspend fun getSmartAmbientEnabled(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.SMART_AMBIENT_ENABLED] ?: false
        }.firstOrNull() ?: false
    }

    override suspend fun setSmartAmbientEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SMART_AMBIENT_ENABLED] = enabled
        }
    }

    override suspend fun getSleepTimerMinutes(): Int {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.SLEEP_TIMER_MINUTES] ?: 0
        }.firstOrNull() ?: 0
    }

    override suspend fun setSleepTimerMinutes(minutes: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SLEEP_TIMER_MINUTES] = minutes
        }
    }
}
