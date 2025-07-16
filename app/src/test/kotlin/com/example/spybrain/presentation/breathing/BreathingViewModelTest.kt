package com.example.spybrain.presentation.breathing

import android.content.Context
import app.cash.turbine.test
import com.example.spybrain.data.repository.BreathingPatternRepository
import com.example.spybrain.domain.repository.BreathingRepository
import com.example.spybrain.domain.usecase.breathing.TrackBreathingSessionUseCase
import com.example.spybrain.domain.usecase.stats.SaveSessionUseCase
import com.example.spybrain.domain.service.IVoiceAssistant
import com.example.spybrain.presentation.breathing.BreathingViewModel
import com.example.spybrain.test.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

class BreathingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var breathingPatternRepository: BreathingPatternRepository
    private lateinit var breathingRepository: BreathingRepository
    private lateinit var trackBreathingSessionUseCase: TrackBreathingSessionUseCase
    private lateinit var saveSessionUseCase: SaveSessionUseCase
    private lateinit var voiceAssistant: IVoiceAssistant
    private lateinit var context: Context
    private lateinit var viewModel: BreathingViewModel

    @Before
    fun setup() {
        breathingPatternRepository = mockk(relaxed = true)
        breathingRepository = mockk(relaxed = true)
        trackBreathingSessionUseCase = mockk(relaxed = true)
        saveSessionUseCase = mockk(relaxed = true)
        voiceAssistant = mockk(relaxed = true)
        context = mockk(relaxed = true)

        coEvery { breathingPatternRepository.getAllPatterns() } returns emptyList()

        viewModel = BreathingViewModel(
            breathingPatternRepository = breathingPatternRepository,
            breathingRepository = breathingRepository,
            trackBreathingSessionUseCase = trackBreathingSessionUseCase,
            saveSessionUseCase = saveSessionUseCase,
            voiceAssistant = voiceAssistant,
            context = context
        )
    }

    @Test
    fun `initial state should be empty`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.patterns.isEmpty())
        }
    }
    // Р”РѕР±Р°РІСЊС‚Рµ РґРѕРїРѕР»РЅРёС‚РµР»СЊРЅС‹Рµ С‚РµСЃС‚С‹ РґР»СЏ СЃРѕР±С‹С‚РёР№ Рё Р»РѕРіРёРєРё ViewModel
}

