package com.example.spybrain.presentation.stats

import app.cash.turbine.test
import com.example.spybrain.domain.usecase.stats.GetStatsUseCase
import com.example.spybrain.presentation.stats.StatsViewModel
import com.example.spybrain.test.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertNotNull

class StatsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getStatsUseCase: GetStatsUseCase
    private lateinit var viewModel: StatsViewModel

    @Before
    fun setup() {
        getStatsUseCase = mockk(relaxed = true)
        coEvery { getStatsUseCase() } returns flowOf(mockk(relaxed = true))
        viewModel = StatsViewModel(getStatsUseCase)
    }

    @Test
    fun `initial state should not be null`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.stats)
        }
    }
    // Добавьте дополнительные тесты для событий и логики ViewModel
} 