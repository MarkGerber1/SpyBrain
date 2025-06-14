package com.example.spybrain.presentation.breathing

import app.cash.turbine.test
import com.example.spybrain.domain.usecase.breathing.GetBreathingPatternsUseCase
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

    private lateinit var getBreathingPatternsUseCase: GetBreathingPatternsUseCase
    private lateinit var viewModel: BreathingViewModel

    @Before
    fun setup() {
        getBreathingPatternsUseCase = mockk(relaxed = true)
        coEvery { getBreathingPatternsUseCase() } returns flowOf(emptyList())
        viewModel = BreathingViewModel(getBreathingPatternsUseCase)
    }

    @Test
    fun `initial state should be empty`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.patterns.isEmpty())
        }
    }
    // Добавьте дополнительные тесты для событий и логики ViewModel
} 