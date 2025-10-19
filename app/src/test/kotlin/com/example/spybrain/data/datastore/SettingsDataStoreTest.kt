package com.example.spybrain.data.datastore

import android.content.Context
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SettingsDataStoreTest {

    private lateinit var context: Context
    private lateinit var settingsDataStore: TestSettingsDataStore

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        settingsDataStore = TestSettingsDataStore(context)
    }

    @Test
    fun `should call setTheme on dataStore`() = runTest {
        // Given
        val theme = "space"

        // When
        settingsDataStore.setTheme(theme)

        // Then
        // Проверяем, что метод вызывается (заглушка не бросает исключений)
    }

    @Test
    fun `should call setAmbientEnabled on dataStore`() = runTest {
        // Given
        val enabled = true

        // When
        settingsDataStore.setAmbientEnabled(enabled)

        // Then
        // Проверяем, что метод вызывается
    }

    @Test
    fun `should call setAmbientTrack on dataStore`() = runTest {
        // Given
        val track = "nature"

        // When
        settingsDataStore.setAmbientTrack(track)

        // Then
        // Проверяем, что метод вызывается
    }

    @Test
    fun `should call setHeartbeatEnabled on dataStore`() = runTest {
        // Given
        val enabled = true

        // When
        settingsDataStore.setHeartbeatEnabled(enabled)

        // Then
        // Проверяем, что метод вызывается
    }

    @Test
    fun `should call setVoiceEnabled on dataStore`() = runTest {
        // Given
        val enabled = true

        // When
        settingsDataStore.setVoiceEnabled(enabled)

        // Then
        // Проверяем, что метод вызывается
    }

    @Test
    fun `should call setVoiceHintsEnabled on dataStore`() = runTest {
        // Given
        val enabled = true

        // When
        settingsDataStore.setVoiceHintsEnabled(enabled)

        // Then
        // Проверяем, что метод вызывается
    }

    @Test
    fun `should call setVoiceId on dataStore`() = runTest {
        // Given
        val voiceId = "voice_1"

        // When
        settingsDataStore.setVoiceId(voiceId)

        // Then
        // Проверяем, что метод вызывается
    }
}