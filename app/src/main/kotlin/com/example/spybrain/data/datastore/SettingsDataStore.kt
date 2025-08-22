package com.example.spybrain.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

/**
 * Класс для управления настройками приложения через DataStore.
 * Позволяет читать и изменять пользовательские настройки, такие как тема, ambient, голосовые параметры и мотивационные очки.
 */
@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    private object PreferencesKey {
        val THEME = stringPreferencesKey("theme")
        val AMBIENT_ENABLED = booleanPreferencesKey("ambient_enabled")
        val AMBIENT_TRACK = stringPreferencesKey("ambient_track")
        val HEARTBEAT_ENABLED = booleanPreferencesKey("heartbeat_enabled")
        val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val VOICE_HINTS_ENABLED = booleanPreferencesKey("voice_hints_enabled")
        val VOICE_ID = stringPreferencesKey("voice_id")
        val MOTIVATIONAL_POINTS = intPreferencesKey("motivational_points")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        // New onboarding and identity keys
        val ONBOARDED = booleanPreferencesKey("onboarded")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AGE = intPreferencesKey("user_age")
        val USER_GENDER = stringPreferencesKey("user_gender")
    }

    /**
     * Темы приложения.
     */
    val themeFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.THEME] ?: "nature"
    }

    /**
     * Включен ли ambient-режим.
     */
    val ambientEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.AMBIENT_ENABLED] ?: false
    }

    /**
     * Трек ambient-музыки.
     */
    val ambientTrackFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.AMBIENT_TRACK] ?: ""
    }

    /**
     * Включен ли heartbeat.
     */
    val heartbeatEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.HEARTBEAT_ENABLED] ?: true
    }

    /**
     * Включен ли голосовой помощник.
     */
    val voiceEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.VOICE_ENABLED] ?: true
    }

    /**
     * Включены ли голосовые подсказки.
     */
    val voiceHintsEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.VOICE_HINTS_ENABLED] ?: true
    }

    /**
     * ID выбранного голоса.
     */
    val voiceIdFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.VOICE_ID] ?: ""
    }

    /**
     * Мотивационные очки.
     */
    val motivationalPointsFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.MOTIVATIONAL_POINTS] ?: 0
    }

    /**
     * Включен ли вибратор.
     */
    val vibrationEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.VIBRATION_ENABLED] ?: true
    }

    // Onboarding and identity flows
    val onboardedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.ONBOARDED] ?: false
    }
    val userNameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.USER_NAME] ?: ""
    }
    val userAgeFlow: Flow<Int?> = dataStore.data.map { preferences ->
        if (preferences.contains(PreferencesKey.USER_AGE)) preferences[PreferencesKey.USER_AGE] else null
    }
    val userGenderFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.USER_GENDER]
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.THEME] = theme
        }
    }

    suspend fun setAmbientEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.AMBIENT_ENABLED] = enabled
        }
    }

    suspend fun setAmbientTrack(trackId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.AMBIENT_TRACK] = trackId
        }
    }

    suspend fun setHeartbeatEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.HEARTBEAT_ENABLED] = enabled
        }
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.VOICE_ENABLED] = enabled
        }
    }

    suspend fun setVoiceHintsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.VOICE_HINTS_ENABLED] = enabled
        }
    }

    suspend fun setVoiceId(voiceId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.VOICE_ID] = voiceId
        }
    }

    suspend fun setMotivationalPoints(points: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.MOTIVATIONAL_POINTS] = points
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.VIBRATION_ENABLED] = enabled
        }
    }

    // Onboarding and identity setters
    suspend fun setOnboarded(onboarded: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.ONBOARDED] = onboarded
        }
    }

    suspend fun setUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.USER_NAME] = name
        }
    }

    suspend fun setUserAge(age: Int?) {
        dataStore.edit { preferences ->
            if (age != null) preferences[PreferencesKey.USER_AGE] = age else preferences.remove(PreferencesKey.USER_AGE)
        }
    }

    suspend fun setUserGender(gender: String?) {
        dataStore.edit { preferences ->
            if (gender != null) preferences[PreferencesKey.USER_GENDER] = gender else preferences.remove(PreferencesKey.USER_GENDER)
        }
    }

    fun getAmbientTrack(): String = runBlocking {
        var trackValue = ""
        dataStore.data.map { preferences ->
            preferences[PreferencesKey.AMBIENT_TRACK] ?: ""
        }.collect {
            trackValue = it
        }
        return@runBlocking trackValue
    }

    fun getAmbientEnabled(): Boolean = runBlocking {
        var enabledValue = false
        dataStore.data.map { preferences ->
            preferences[PreferencesKey.AMBIENT_ENABLED] ?: false
        }.collect {
            enabledValue = it
        }
        return@runBlocking enabledValue
    }

    fun getMotivationalPoints(): Int = runBlocking {
        var pointsValue = 0
        dataStore.data.map { preferences ->
            preferences[PreferencesKey.MOTIVATIONAL_POINTS] ?: 0
        }.collect {
            pointsValue = it
        }
        return@runBlocking pointsValue
    }

    fun getVibrationEnabled(): Boolean = runBlocking {
        var vibrationValue = true
        dataStore.data.map { preferences ->
            preferences[PreferencesKey.VIBRATION_ENABLED] ?: true
        }.collect {
            vibrationValue = it
        }
        return@runBlocking vibrationValue
    }
}
