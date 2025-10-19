package com.example.spybrain.data.datastore

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Тестовая реализация SettingsDataStore для unit тестов.
 * Не требует реального Context и DataStore.
 */
class TestSettingsDataStore(context: Context) : SettingsDataStore(context) {

    override val themeFlow: Flow<String> = flowOf("nature")
    override val ambientEnabledFlow: Flow<Boolean> = flowOf(false)
    override val ambientTrackFlow: Flow<String> = flowOf("")
    override val heartbeatEnabledFlow: Flow<Boolean> = flowOf(true)
    override val voiceEnabledFlow: Flow<Boolean> = flowOf(true)
    override val voiceHintsEnabledFlow: Flow<Boolean> = flowOf(true)
    override val vibrationEnabledFlow: Flow<Boolean> = flowOf(true)
    override val voiceIdFlow: Flow<String> = flowOf("")

    override suspend fun setTheme(theme: String) {
        // Заглушка для тестов
    }

    override suspend fun setAmbientEnabled(enabled: Boolean) {
        // Заглушка для тестов
    }

    override suspend fun setAmbientTrack(track: String) {
        // Заглушка для тестов
    }

    override suspend fun setHeartbeatEnabled(enabled: Boolean) {
        // Заглушка для тестов
    }

    override suspend fun setVoiceEnabled(enabled: Boolean) {
        // Заглушка для тестов
    }

    override suspend fun setVoiceHintsEnabled(enabled: Boolean) {
        // Заглушка для тестов
    }

    override suspend fun setVoiceId(voiceId: String) {
        // Заглушка для тестов
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        // Заглушка для тестов
    }
}