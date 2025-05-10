package com.example.spybrain.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Расширение для доступа к DataStore
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context
) {
    companion object {
        val THEME_KEY = stringPreferencesKey("theme")
        val AMBIENT_ENABLED_KEY = booleanPreferencesKey("ambient_enabled")
        val AMBIENT_TRACK_KEY = stringPreferencesKey("ambient_track")
        val HEARTBEAT_ENABLED_KEY = booleanPreferencesKey("heartbeat_enabled")
        val VOICE_ENABLED_KEY = booleanPreferencesKey("voice_enabled")
        val VOICE_HINTS_ENABLED_KEY = booleanPreferencesKey("voice_hints_enabled")
        val VOICE_ID_KEY = stringPreferencesKey("voice_id")
    }

    private val dataStore = context.settingsDataStore

    val themeFlow: Flow<String> = dataStore.data
        .map { prefs -> prefs[THEME_KEY] ?: "water" }

    val ambientEnabledFlow: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[AMBIENT_ENABLED_KEY] ?: false }

    val ambientTrackFlow: Flow<String> = dataStore.data
        .map { prefs -> prefs[AMBIENT_TRACK_KEY] ?: "" }

    val heartbeatEnabledFlow: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[HEARTBEAT_ENABLED_KEY] ?: false }

    val voiceEnabledFlow: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[VOICE_ENABLED_KEY] ?: false }

    val voiceHintsEnabledFlow: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[VOICE_HINTS_ENABLED_KEY] ?: false }

    val voiceIdFlow: Flow<String> = dataStore.data
        .map { prefs -> prefs[VOICE_ID_KEY] ?: "" }

    suspend fun setTheme(theme: String) {
        dataStore.edit { prefs -> prefs[THEME_KEY] = theme }
    }

    suspend fun setAmbientEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[AMBIENT_ENABLED_KEY] = enabled }
    }

    suspend fun setAmbientTrack(track: String) {
        dataStore.edit { prefs -> prefs[AMBIENT_TRACK_KEY] = track }
    }

    suspend fun setHeartbeatEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[HEARTBEAT_ENABLED_KEY] = enabled }
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[VOICE_ENABLED_KEY] = enabled }
    }

    suspend fun setVoiceHintsEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[VOICE_HINTS_ENABLED_KEY] = enabled }
    }

    suspend fun setVoiceId(voiceId: String) {
        dataStore.edit { prefs -> prefs[VOICE_ID_KEY] = voiceId }
    }
} 