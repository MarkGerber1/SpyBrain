import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.TestCoroutineDispatcher
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.slot
import io.mockk.just
import io.mockk.Runs
﻿package com.example.spybrain.presentation.settings

import android.content.Context
import android.content.Intent
import com.example.spybrain.data.datastore.SettingsDataStore
import com.example.spybrain.domain.usecase.meditation.GetMeditationsUseCase
import com.example.spybrain.presentation.settings.SettingsContract
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var settingsDataStore: SettingsDataStore

    @MockK
    private lateinit var getMeditationsUseCase: GetMeditationsUseCase

    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // РњРѕРєР°РµРј РїРѕС‚РѕРєРё РґР°РЅРЅС‹С…
        every { settingsDataStore.themeFlow } returns flowOf("nature")
        every { settingsDataStore.ambientEnabledFlow } returns flowOf(false)
        every { settingsDataStore.ambientTrackFlow } returns flowOf("")
        every { settingsDataStore.heartbeatEnabledFlow } returns flowOf(true)
        every { settingsDataStore.voiceEnabledFlow } returns flowOf(true)
        every { settingsDataStore.voiceHintsEnabledFlow } returns flowOf(true)
        every { settingsDataStore.vibrationEnabledFlow } returns flowOf(true)
        every { settingsDataStore.voiceIdFlow } returns flowOf("")

        every { getMeditationsUseCase() } returns flowOf(emptyList())

        // РњРѕРєР°РµРј СЃРёРЅС…СЂРѕРЅРЅС‹Рµ РјРµС‚РѕРґС‹
        coEvery { settingsDataStore.getAmbientTrack() } returns ""
        coEvery { settingsDataStore.getAmbientEnabled() } returns false
        coEvery { settingsDataStore.setTheme(any()) } just Runs
        coEvery { settingsDataStore.setAmbientEnabled(any()) } just Runs
        coEvery { settingsDataStore.setAmbientTrack(any()) } just Runs
        coEvery { settingsDataStore.setHeartbeatEnabled(any()) } just Runs
        coEvery { settingsDataStore.setVoiceEnabled(any()) } just Runs
        coEvery { settingsDataStore.setVoiceHintsEnabled(any()) } just Runs
        coEvery { settingsDataStore.setVoiceId(any()) } just Runs
        coEvery { settingsDataStore.setVibrationEnabled(any()) } just Runs

        viewModel = SettingsViewModel(settingsDataStore, getMeditationsUseCase, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        val initialState = viewModel.uiState.value

        assertEquals("nature", initialState.theme)
        assertFalse(initialState.ambientEnabled)
        assertTrue(initialState.heartbeatEnabled)
        assertTrue(initialState.voiceEnabled)
        assertTrue(initialState.voiceHintsEnabled)
        assertTrue(initialState.vibrationEnabled)
    }

    @Test
    fun `themeSelected should update theme and show toast`() = runTest {
        val newTheme = "water"

        viewModel.setEvent(SettingsContract.Event.ThemeSelected(newTheme))

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsDataStore.setTheme(newTheme) }
        assertEquals(newTheme, viewModel.uiState.value.theme)
    }

    @Test
    fun `ambientToggled should update ambient setting`() = runTest {
        viewModel.setEvent(SettingsContract.Event.AmbientToggled(true))

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsDataStore.setAmbientEnabled(true) }
        assertTrue(viewModel.uiState.value.ambientEnabled)
    }

    @Test
    fun `heartbeatToggled should update heartbeat setting and show toast`() = runTest {
        viewModel.setEvent(SettingsContract.Event.HeartbeatToggled(false))

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsDataStore.setHeartbeatEnabled(false) }
        assertFalse(viewModel.uiState.value.heartbeatEnabled)
    }

    @Test
    fun `voiceHintsToggled should update voice hints setting and show toast`() = runTest {
        viewModel.setEvent(SettingsContract.Event.VoiceHintsToggled(false))

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsDataStore.setVoiceHintsEnabled(false) }
        assertFalse(viewModel.uiState.value.voiceHintsEnabled)
    }

    @Test
    fun `voiceIdSelected should update voice ID and show toast`() = runTest {
        val voiceId = "test_voice_id"

        viewModel.setEvent(SettingsContract.Event.VoiceIdSelected(voiceId))

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsDataStore.setVoiceId(voiceId) }
        assertEquals(voiceId, viewModel.uiState.value.voiceId)
    }

    @Test
    fun `vibrationToggled should update vibration setting and show toast`() = runTest {
        viewModel.setEvent(SettingsContract.Event.VibrationToggled(false))

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsDataStore.setVibrationEnabled(false) }
        assertFalse(viewModel.uiState.value.vibrationEnabled)
    }

    @Test
    fun `languageChanged should update language and show toast`() = runTest {
        val newLanguage = "en"

        viewModel.setEvent(SettingsContract.Event.LanguageChanged(newLanguage))

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(newLanguage, viewModel.uiState.value.currentLanguage)
    }

    @Test
    fun `ambientTrackSelected should update track when ambient is enabled`() = runTest {
        coEvery { settingsDataStore.getAmbientEnabled() } returns true
        val trackId = "test_track"

        viewModel.setEvent(SettingsContract.Event.AmbientTrackSelected(trackId))

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsDataStore.setAmbientTrack(trackId) }
        assertEquals(trackId, viewModel.uiState.value.ambientTrack)
    }
}

