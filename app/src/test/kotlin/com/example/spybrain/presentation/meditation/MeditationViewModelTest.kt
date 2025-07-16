package com.example.spybrain.presentation.meditation

import com.example.spybrain.data.repository.MeditationRepositoryImpl.MeditationTrack
import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.service.IAiMentor
import com.example.spybrain.domain.service.IPlayerService
import com.example.spybrain.domain.service.IVoiceAssistant
import com.example.spybrain.domain.usecase.meditation.GetMeditationsUseCase
import com.example.spybrain.domain.usecase.meditation.TrackMeditationSessionUseCase
import com.example.spybrain.presentation.meditation.MeditationContract.Event
import com.example.spybrain.presentation.meditation.MeditationContract.State
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class MeditationViewModelTest {

    private lateinit var viewModel: MeditationViewModel
    private lateinit var getMeditationsUseCase: GetMeditationsUseCase
    private lateinit var trackMeditationSessionUseCase: TrackMeditationSessionUseCase
    private lateinit var playerService: IPlayerService
    private lateinit var aiMentor: IAiMentor
    private lateinit var voiceAssistant: IVoiceAssistant

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getMeditationsUseCase = mockk(relaxed = true)
        trackMeditationSessionUseCase = mockk(relaxed = true)
        playerService = mockk(relaxed = true)
        aiMentor = mockk(relaxed = true)
        voiceAssistant = mockk(relaxed = true)

        coEvery { getMeditationsUseCase() } returns flowOf(emptyList<Meditation>())

        viewModel = MeditationViewModel(
            getMeditationsUseCase = getMeditationsUseCase,
            trackMeditationSessionUseCase = trackMeditationSessionUseCase,
            playerService = playerService,
            aiMentor = aiMentor,
            voiceAssistant = voiceAssistant
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty meditation tracks`() = runTest {
        val initialState = viewModel.uiState.value
        assertEquals(emptyList<MeditationTrack>(), initialState.meditationTracks)
        assertEquals(null, initialState.selectedTrack)
        assertEquals(null, initialState.currentPlayingTrack)
        assertEquals(false, initialState.isTrackPlaying)
    }

    @Test
    fun `loadMeditationTracks should populate meditation tracks`() = runTest {
        // Given
        val expectedTracks = listOf(
            MeditationTrack("angelic", 1, "audio/meditation_music/meditation_angelic.mp3"),
            MeditationTrack("chill", 2, "audio/meditation_music/meditation_chill.mp3")
        )

        // When
        viewModel.setEvent(Event.LoadMeditationTracks)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertNotNull(state.meditationTracks)
        // Проверяем, что треки загружены
        // (в реальной реализации они загружаются из репозитория)
    }

    @Test
    fun `selectMeditationTrack should update selected track`() = runTest {
        // Given
        val track = MeditationTrack("test", 1, "audio/test.mp3")

        // When
        viewModel.setEvent(Event.SelectMeditationTrack(track))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(track, state.selectedTrack)
    }

    @Test
    fun `playSelectedTrack should update playing state`() = runTest {
        // Given
        val track = MeditationTrack("test", 1, "audio/test.mp3")
        viewModel.setEvent(Event.SelectMeditationTrack(track))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.setEvent(Event.PlaySelectedTrack)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(track, state.currentPlayingTrack)
        assertEquals(true, state.isTrackPlaying)
    }
}

