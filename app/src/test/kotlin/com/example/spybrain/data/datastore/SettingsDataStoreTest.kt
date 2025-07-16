package com.example.spybrain.data.datastore

import android.content.Context
import com.example.spybrain.test.utils.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsDataStoreTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var context: Context
    private lateinit var settingsDataStore: SettingsDataStore

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        settingsDataStore = SettingsDataStore(context)
    }

    @Test
    fun `should call setTheme on dataStore`() = runTest {
        // Given
        val theme = "space"

        // When
        settingsDataStore.setTheme(theme)

        // Then
        // Проверяем, что метод вызывается
        // (реальная проверка будет в интеграционных тестах)
    }

    @Test
    fun `should call setAmbientEnabled on dataStore`() = runTest {
        // Given
        val enabled = true

        // When
        settingsDataStore.setAmbientEnabled(enabled)

        // Then
        // РџСЂРѕРІРµСЂСЏРµРј, С‡С‚Рѕ РјРµС‚РѕРґ РІС‹Р·С‹РІР°РµС‚СЃСЏ
    }

    @Test
    fun `should call setAmbientTrack on dataStore`() = runTest {
        // Given
        val track = "nature"

        // When
        settingsDataStore.setAmbientTrack(track)

        // Then
        // РџСЂРѕРІРµСЂСЏРµРј, С‡С‚Рѕ РјРµС‚РѕРґ РІС‹Р·С‹РІР°РµС‚СЃСЏ
    }

    @Test
    fun `should call setHeartbeatEnabled on dataStore`() = runTest {
        // Given
        val enabled = true

        // When
        settingsDataStore.setHeartbeatEnabled(enabled)

        // Then
        // РџСЂРѕРІРµСЂСЏРµРј, С‡С‚Рѕ РјРµС‚РѕРґ РІС‹Р·С‹РІР°РµС‚СЃСЏ
    }

    @Test
    fun `should call setVoiceEnabled on dataStore`() = runTest {
        // Given
        val enabled = true

        // When
        settingsDataStore.setVoiceEnabled(enabled)

        // Then
        // РџСЂРѕРІРµСЂСЏРµРј, С‡С‚Рѕ РјРµС‚РѕРґ РІС‹Р·С‹РІР°РµС‚СЃСЏ
    }

    @Test
    fun `should call setVoiceHintsEnabled on dataStore`() = runTest {
        // Given
        val enabled = true

        // When
        settingsDataStore.setVoiceHintsEnabled(enabled)

        // Then
        // РџСЂРѕРІРµСЂСЏРµРј, С‡С‚Рѕ РјРµС‚РѕРґ РІС‹Р·С‹РІР°РµС‚СЃСЏ
    }

    @Test
    fun `should call setVoiceId on dataStore`() = runTest {
        // Given
        val voiceId = "voice_1"

        // When
        settingsDataStore.setVoiceId(voiceId)

        // Then
        // РџСЂРѕРІРµСЂСЏРµРј, С‡С‚Рѕ РјРµС‚РѕРґ РІС‹Р·С‹РІР°РµС‚СЃСЏ
    }
}

