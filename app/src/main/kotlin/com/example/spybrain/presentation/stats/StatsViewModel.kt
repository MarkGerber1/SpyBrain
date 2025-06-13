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
import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.example.spybrain.domain.error.ErrorHandler
import timber.log.Timber

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getOverallStatsUseCase: GetOverallStatsUseCase,
    private val getSessionHistoryUseCase: GetSessionHistoryUseCase,
    private val getBreathingHistoryUseCase: GetBreathingHistoryUseCase
) : BaseViewModel<StatsContract.Event, StatsContract.State, StatsContract.Effect>() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "Ошибка в StatsViewModel корутине")
        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(exception))
        setEffect { StatsContract.Effect.ShowError(uiError) }
    }

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
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                getOverallStatsUseCase()
                    .onStart { setState { copy(isLoading = true, error = null) } }
                    .catch { error ->
                        Timber.e(error, "Ошибка загрузки статистики")
                        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(error))
                        setState { copy(isLoading = false, error = uiError) }
                        setEffect { StatsContract.Effect.ShowError(uiError) }
                    }
                    .collect { stats ->
                        setState { copy(isLoading = false, overallStats = stats) }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Критическая ошибка при загрузке статистики")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setState { copy(isLoading = false, error = uiError) }
                setEffect { StatsContract.Effect.ShowError(uiError) }
            }
        }
    }

    override fun onCleared() {
        try {
            // Дополнительная очистка ресурсов при необходимости
            super.onCleared()
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при очистке StatsViewModel")
        }
    }
} 