package com.example.spybrain.presentation.stats

import app.cash.turbine.test
import com.example.spybrain.domain.usecase.stats.GetOverallStatsUseCase
import com.example.spybrain.domain.usecase.stats.GetSessionHistoryUseCase
import com.example.spybrain.domain.usecase.stats.GetBreathingHistoryUseCase
import com.example.spybrain.domain.usecase.achievements.CheckAchievementsUseCase
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

    private lateinit var getOverallStatsUseCase: GetOverallStatsUseCase
    private lateinit var getSessionHistoryUseCase: GetSessionHistoryUseCase
    private lateinit var getBreathingHistoryUseCase: GetBreathingHistoryUseCase
    private lateinit var checkAchievementsUseCase: CheckAchievementsUseCase
    private lateinit var viewModel: StatsViewModel

    @Before
    fun setup() {
        getOverallStatsUseCase = mockk(relaxed = true)
        getSessionHistoryUseCase = mockk(relaxed = true)
        getBreathingHistoryUseCase = mockk(relaxed = true)
        checkAchievementsUseCase = mockk(relaxed = true)

        coEvery { getOverallStatsUseCase() } returns flowOf(mockk(relaxed = true))
        coEvery { getSessionHistoryUseCase() } returns flowOf(emptyList())
        coEvery { getBreathingHistoryUseCase() } returns flowOf(emptyList())
        coEvery { checkAchievementsUseCase(any()) } returns emptyList()

        viewModel = StatsViewModel(
            getOverallStatsUseCase = getOverallStatsUseCase,
            getSessionHistoryUseCase = getSessionHistoryUseCase,
            getBreathingHistoryUseCase = getBreathingHistoryUseCase,
            checkAchievementsUseCase = checkAchievementsUseCase
        )
    }

    @Test
    fun `initial state should not be null`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.stats)
        }
    }
    // Р”РѕР±Р°РІСЊС‚Рµ РґРѕРїРѕР»РЅРёС‚РµР»СЊРЅС‹Рµ С‚РµСЃС‚С‹ РґР»СЏ СЃРѕР±С‹С‚РёР№ Рё Р»РѕРіРёРєРё ViewModel
}

