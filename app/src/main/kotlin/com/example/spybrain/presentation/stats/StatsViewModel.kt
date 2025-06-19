package com.example.spybrain.presentation.stats

import androidx.lifecycle.viewModelScope
import com.example.spybrain.presentation.base.BaseViewModel
import com.example.spybrain.presentation.stats.StatsContract
import com.example.spybrain.domain.usecase.stats.GetOverallStatsUseCase
import com.example.spybrain.domain.usecase.stats.GetSessionHistoryUseCase
import com.example.spybrain.domain.usecase.stats.GetBreathingHistoryUseCase
import com.example.spybrain.domain.usecase.achievements.CheckAchievementsUseCase
import com.example.spybrain.domain.model.Stats
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
import java.util.*

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getOverallStatsUseCase: GetOverallStatsUseCase,
    private val getSessionHistoryUseCase: GetSessionHistoryUseCase,
    private val getBreathingHistoryUseCase: GetBreathingHistoryUseCase,
    private val checkAchievementsUseCase: CheckAchievementsUseCase
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
            StatsContract.Event.RefreshStats -> loadStatsAndHistory()
            StatsContract.Event.ShowMotivationalMessage -> showMotivationalMessage()
        }
    }

    private fun loadStatsAndHistory() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                setState { copy(isLoading = true, error = null) }
                
                getOverallStatsUseCase()
                    .onStart { setState { copy(isLoading = true, error = null) } }
                    .catch { error ->
                        Timber.e(error, "Ошибка загрузки статистики")
                        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(error))
                        setState { copy(isLoading = false, error = uiError) }
                        setEffect { StatsContract.Effect.ShowError(uiError) }
                    }
                    .collect { stats ->
                        setState { copy(isLoading = false, stats = stats) }
                        
                        // Проверяем достижения
                        checkAchievements(stats)
                        
                        // Показываем мотивационное сообщение
                        showMotivationalMessage()
                        
                        Timber.d("Статистика загружена: ${stats.completedMeditationSessions + stats.completedBreathingSessions} сессий")
                    }
            } catch (e: Exception) {
                Timber.e(e, "Критическая ошибка при загрузке статистики")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setState { copy(isLoading = false, error = uiError) }
                setEffect { StatsContract.Effect.ShowError(uiError) }
            }
        }
    }
    
    private fun checkAchievements(stats: Stats) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                val achievements = checkAchievementsUseCase(stats)
                if (achievements.isNotEmpty()) {
                    setEffect { StatsContract.Effect.ShowAchievements(achievements) }
                }
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при проверке достижений")
            }
        }
    }
    
    private fun showMotivationalMessage() {
        val stats = uiState.value.stats
        val message = generateMotivationalMessage(stats)
        setEffect { StatsContract.Effect.ShowMotivationalMessage(message) }
    }
    
    private fun generateMotivationalMessage(stats: Stats?): String {
        if (stats == null) return "Начните свой путь к осознанности сегодня!"
        
        val totalSessions = stats.completedMeditationSessions + stats.completedBreathingSessions
        
        return when {
            totalSessions == 0 -> "Начните свой путь к осознанности сегодня!"
            totalSessions < 5 -> "Отличное начало! Каждый шаг важен."
            totalSessions < 10 -> "Вы на правильном пути! Продолжайте в том же духе."
            totalSessions < 25 -> "Впечатляющий прогресс! Вы становитесь сильнее."
            totalSessions < 50 -> "Невероятно! Вы настоящий мастер осознанности."
            totalSessions < 100 -> "Легендарный уровень! Вы вдохновляете других."
            else -> "Вы достигли просветления! Поделитесь мудростью с миром."
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