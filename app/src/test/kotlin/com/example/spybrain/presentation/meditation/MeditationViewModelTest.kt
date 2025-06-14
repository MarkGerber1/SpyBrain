package com.example.spybrain.presentation.meditation

import app.cash.turbine.test
import com.example.spybrain.domain.usecase.meditation.GetMeditationsUseCase
import com.example.spybrain.presentation.meditation.MeditationViewModel
import com.example.spybrain.test.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MeditationViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getMeditationsUseCase: GetMeditationsUseCase
    private lateinit var viewModel: MeditationViewModel

    @Before
    fun setup() {
        getMeditationsUseCase = mockk(relaxed = true)
        coEvery { getMeditationsUseCase() } returns flowOf(emptyList())
        viewModel = MeditationViewModel(getMeditationsUseCase)
    }

    @Test
    fun `initial state should be empty`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.meditations.isEmpty())
        }
    }

    // Добавьте дополнительные тесты для событий и логики ViewModel
} 