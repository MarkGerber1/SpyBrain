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
import kotlinx.coroutines.flow.first
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
        val AMBIENT_VOLUME = intPreferencesKey("ambient_volume_percent")
        val HEARTBEAT_ENABLED = booleanPreferencesKey("heartbeat_enabled")
        val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val VOICE_HINTS_ENABLED = booleanPreferencesKey("voice_hints_enabled")
        val VOICE_ID = stringPreferencesKey("voice_id")
        val VOICE_RATE = intPreferencesKey("voice_rate_percent")
        val VOICE_PITCH = intPreferencesKey("voice_pitch_percent")
        val MOTIVATIONAL_POINTS = intPreferencesKey("motivational_points")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AGE = intPreferencesKey("user_age")
        val USER_GENDER = stringPreferencesKey("user_gender")
        val BACKGROUND_STYLE = stringPreferencesKey("background_style")
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
     * Громкость ambient (0.0..1.0), хранится как проценты 0..100
     */
    val ambientVolumeFlow: Flow<Float> = dataStore.data.map { preferences ->
        val percent = preferences[PreferencesKey.AMBIENT_VOLUME] ?: 50
        (percent.coerceIn(0, 100)) / 100f
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

    val voiceRateFlow: Flow<Float> = dataStore.data.map { preferences ->
        val percent = preferences[PreferencesKey.VOICE_RATE] ?: 85
        (percent.coerceIn(30, 200)) / 100f
    }

    val voicePitchFlow: Flow<Float> = dataStore.data.map { preferences ->
        val percent = preferences[PreferencesKey.VOICE_PITCH] ?: 100
        (percent.coerceIn(50, 200)) / 100f
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

    /**
     * Имя пользователя.
     */
    val userNameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.USER_NAME] ?: ""
    }

    /**
     * Возраст пользователя.
     */
    val userAgeFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.USER_AGE] ?: 0
    }

    /**
     * Пол пользователя ("male" | "female" | "other").
     */
    val userGenderFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.USER_GENDER] ?: "other"
    }

    /**
     * Стиль живого фона (auto|ocean|waterfall|clouds|stars|forest|mountains|rain|sky).
     */
    val backgroundStyleFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.BACKGROUND_STYLE] ?: "auto"
    }

    /**
     * Установить тему приложения.
     * @param theme Тема.
     */
    suspend fun setTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.THEME] = theme
        }
    }

    /**
     * Включить/выключить ambient-режим.
     * @param enabled Включенно.
     */
    suspend fun setAmbientEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.AMBIENT_ENABLED] = enabled
        }
    }

    /**
     * Установить трек ambient-музыки.
     * @param track Трек.
     */
    suspend fun setAmbientTrack(trackId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.AMBIENT_TRACK] = trackId
        }
    }

    /**
     * Установить громкость ambient в процентах (0..100)
     */
    suspend fun setAmbientVolumePercent(percent: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.AMBIENT_VOLUME] = percent.coerceIn(0, 100)
        }
    }

    /**
     * Включить/выключить heartbeat.
     * @param enabled Включенно.
     */
    suspend fun setHeartbeatEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.HEARTBEAT_ENABLED] = enabled
        }
    }

    /**
     * Включить/выключить голосовой помощник.
     * @param enabled Включенно.
     */
    suspend fun setVoiceEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.VOICE_ENABLED] = enabled
        }
    }

    /**
     * Включить/выключить голосовые подсказки.
     * @param enabled Включенно.
     */
    suspend fun setVoiceHintsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.VOICE_HINTS_ENABLED] = enabled
        }
    }

    /**
     * Установить ID голоса.
     * @param voiceId ID голоса.
     */
    suspend fun setVoiceId(voiceId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.VOICE_ID] = voiceId
        }
    }

    suspend fun setVoiceRatePercent(percent: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.VOICE_RATE] = percent.coerceIn(30, 200)
        }
    }

    suspend fun setVoicePitchPercent(percent: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.VOICE_PITCH] = percent.coerceIn(50, 200)
        }
    }

    /**
     * Установить мотивационные очки.
     * @param points Количество очков.
     */
    suspend fun setMotivationalPoints(points: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.MOTIVATIONAL_POINTS] = points
        }
    }

    /**
     * Включить/выключить вибратор.
     * @param enabled Включенно.
     */
    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun setUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.USER_NAME] = name
        }
    }

    suspend fun setUserAge(age: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.USER_AGE] = age.coerceAtLeast(0)
        }
    }

    suspend fun setUserGender(gender: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.USER_GENDER] = gender
        }
    }

    suspend fun setBackgroundStyle(style: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.BACKGROUND_STYLE] = style
        }
    }

    /**
     * Получить трек ambient-музыки.
     * @return Flow с треком.
     */
    fun getAmbientTrack(): String = runBlocking {
        val preferences = dataStore.data.first()
        preferences[PreferencesKey.AMBIENT_TRACK] ?: ""
    }

    /**
     * Получить состояние ambient-режима.
     * @return Flow с состоянием.
     */
    fun getAmbientEnabled(): Boolean = runBlocking {
        val preferences = dataStore.data.first()
        preferences[PreferencesKey.AMBIENT_ENABLED] ?: false
    }

    fun getAmbientVolume(): Float = runBlocking {
        val preferences = dataStore.data.first()
        val percent = preferences[PreferencesKey.AMBIENT_VOLUME] ?: 50
        (percent.coerceIn(0, 100)) / 100f
    }

    /**
     * Получить мотивационные очки.
     * @return Flow с очками.
     */
    fun getMotivationalPoints(): Int = runBlocking {
        val preferences = dataStore.data.first()
        preferences[PreferencesKey.MOTIVATIONAL_POINTS] ?: 0
    }

    /**
     * Получить состояние вибратора.
     * @return Flow с состоянием.
     */
    fun getVibrationEnabled(): Boolean = runBlocking {
        val preferences = dataStore.data.first()
        preferences[PreferencesKey.VIBRATION_ENABLED] ?: true
    }

    /**
     * Получить ID голоса.
     * @return Flow с ID.
     */
    fun getVoiceId(): String = runBlocking {
        val preferences = dataStore.data.first()
        preferences[PreferencesKey.VOICE_ID] ?: ""
    }

    fun getVoiceRate(): Float = runBlocking {
        val preferences = dataStore.data.first()
        val percent = preferences[PreferencesKey.VOICE_RATE] ?: 85
        (percent.coerceIn(30, 200)) / 100f
    }

    fun getVoicePitch(): Float = runBlocking {
        val preferences = dataStore.data.first()
        val percent = preferences[PreferencesKey.VOICE_PITCH] ?: 100
        (percent.coerceIn(50, 200)) / 100f
    }

    fun getUserName(): String = runBlocking {
        val preferences = dataStore.data.first()
        preferences[PreferencesKey.USER_NAME] ?: ""
    }

    fun getUserAge(): Int = runBlocking {
        val preferences = dataStore.data.first()
        preferences[PreferencesKey.USER_AGE] ?: 0
    }

    fun getUserGender(): String = runBlocking {
        val preferences = dataStore.data.first()
        preferences[PreferencesKey.USER_GENDER] ?: "other"
    }

    fun getBackgroundStyle(): String = runBlocking {
        val preferences = dataStore.data.first()
        preferences[PreferencesKey.BACKGROUND_STYLE] ?: "auto"
    }
}
