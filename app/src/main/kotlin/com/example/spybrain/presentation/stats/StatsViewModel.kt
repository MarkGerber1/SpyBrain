package com.example.spybrain.presentation.stats

import androidx.lifecycle.viewModelScope
import com.example.spybrain.presentation.base.BaseViewModel
import com.example.spybrain.presentation.stats.StatsContract
import com.example.spybrain.domain.usecase.stats.GetOverallStatsUseCase
import com.example.spybrain.domain.usecase.stats.GetSessionHistoryUseCase
import com.example.spybrain.domain.usecase.stats.GetBreathingHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.example.spybrain.domain.error.ErrorHandler // FIXME билд-фикс 09.05.2025

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getOverallStatsUseCase: GetOverallStatsUseCase,
    private val getSessionHistoryUseCase: GetSessionHistoryUseCase,
    private val getBreathingHistoryUseCase: GetBreathingHistoryUseCase
) : BaseViewModel<StatsContract.Event, StatsContract.State, StatsContract.Effect>() { // TODO: Добавить все необходимые юнит-тесты для ViewModel

    // Expose breathing history as StateFlow
    val breathingHistory = getBreathingHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Expose meditation history as StateFlow
    val meditationHistory = getSessionHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    override fun createInitialState(): StatsContract.State = StatsContract.State()

    init {
        setEvent(StatsContract.Event.LoadStatsAndHistory)
    }

    override fun handleEvent(event: StatsContract.Event) {
        when (event) {
            StatsContract.Event.LoadStatsAndHistory -> loadStatsAndHistory()
        }
    }

    private fun loadStatsAndHistory() {
        viewModelScope.launch {
            getOverallStatsUseCase()
                .onStart { setState { copy(isLoading = true, error = null) } }
                .catch { e ->
                    setState { copy(isLoading = false, error = ErrorHandler.mapToUiError(ErrorHandler.handle(e))) }
                    setEffect { StatsContract.Effect.ShowError(ErrorHandler.mapToUiError(ErrorHandler.handle(e))) }
                }
                .collect { stats ->
                    setState { copy(stats = stats) }
                }

            getSessionHistoryUseCase()
                .catch { e ->
                    setEffect { StatsContract.Effect.ShowError(ErrorHandler.mapToUiError(ErrorHandler.handle(e))) }
                }
                .collect { history ->
                    setState { copy(sessionHistory = history, isLoading = false) }
                }
            // Load breathing sessions history
            getBreathingHistoryUseCase()
                .catch { e ->
                     setEffect { StatsContract.Effect.ShowError(ErrorHandler.mapToUiError(ErrorHandler.handle(e))) }
                }
                .collect { breathingHistory ->
                     setState { copy(breathingHistory = breathingHistory, isLoading = false) }
                }
        }
    }
} 