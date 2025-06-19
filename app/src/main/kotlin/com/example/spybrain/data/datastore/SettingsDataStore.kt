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
    }

    // Theme flow
    val themeFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.THEME] ?: "nature"
    }

    // Ambient music enabled flow
    val ambientEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.AMBIENT_ENABLED] ?: false
    }

    // Ambient track flow
    val ambientTrackFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.AMBIENT_TRACK] ?: ""
    }

    // Heartbeat enabled flow
    val heartbeatEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.HEARTBEAT_ENABLED] ?: true
    }

    // Voice enabled flow
    val voiceEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.VOICE_ENABLED] ?: true
    }
    
    // Voice hints enabled flow (for meditation guidance)
    val voiceHintsEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.VOICE_HINTS_ENABLED] ?: true
    }
    
    // Voice ID flow
    val voiceIdFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.VOICE_ID] ?: ""
    }
    
    // Motivational points flow
    val motivationalPointsFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.MOTIVATIONAL_POINTS] ?: 0
    }
    
    // Vibration enabled flow
    val vibrationEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.VIBRATION_ENABLED] ?: true
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
    
    // Синхронные методы для получения настроек
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
    
    fun getVoiceId(): String = runBlocking {
        var voiceIdValue = ""
        dataStore.data.map { preferences ->
            preferences[PreferencesKey.VOICE_ID] ?: ""
        }.collect { 
            voiceIdValue = it
        }
        return@runBlocking voiceIdValue
    }
} 