package com.example.spybrain.presentation.stats

import java.util.Date
import java.util.Locale
import java.util.UUID
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEffect
/**
 */
@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getOverallStatsUseCase: GetOverallStatsUseCase,
    private val getSessionHistoryUseCase: GetSessionHistoryUseCase,
    private val getBreathingHistoryUseCase: GetBreathingHistoryUseCase,
    private val checkAchievementsUseCase: CheckAchievementsUseCase
) : BaseViewModel<StatsContract.Event, StatsContract.State, StatsContract.Effect>() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "РћС€РёР±РєР° РІ StatsViewModel РєРѕСЂСѓС‚РёРЅРµ")
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
            is StatsContract.Event.ShowMotivationalMessage -> showMotivationalMessage(event.message)
        }
    }

    private fun loadStatsAndHistory() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                setState { copy(isLoading = true, error = null) }

                getOverallStatsUseCase()
                    .onStart { setState { copy(isLoading = true, error = null) } }
                    .catch { error ->
                        Timber.e(error, "РћС€РёР±РєР° Р·Р°РіСЂСѓР·РєРё СЃС‚Р°С‚РёСЃС‚РёРєРё")
                        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(error))
                        setState { copy(isLoading = false, error = uiError) }
                        setEffect { StatsContract.Effect.ShowError(uiError) }
                    }
                    .collect { stats ->
                        setState { copy(isLoading = false, stats = stats) }

                        // РџСЂРѕРІРµСЂСЏРµРј РґРѕСЃС‚РёР¶РµРЅРёСЏ
                        checkAchievements(stats)

                        // РџРѕРєР°Р·С‹РІР°РµРј РјРѕС‚РёРІР°С†РёРѕРЅРЅРѕРµ СЃРѕРѕР±С‰РµРЅРёРµ
                        showMotivationalMessage()

                        Timber.d(
                            "Статистика загружена: " +
                                "${stats.completedMeditationSessions + stats.completedBreathingSessions} сессий"
                        )
                    }
            } catch (e: Exception) {
                Timber.e(e, "РљСЂРёС‡РµСЃРєР°СЏ РѕС€РёР±РєР° РїСЂРё Р·Р°РіСЂСѓР·РєРµ СЃС‚Р°С‚РёСЃС‚РёРєРё")
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
                Timber.e(e, "РћС€РёР±РєР° РїСЂРё РїСЂРѕРІРµСЂРєРµ РґРѕСЃС‚РёР¶РµРЅРёР№")
            }
        }
    }

    private fun showMotivationalMessage(customMessage: String? = null) {
        val message = customMessage ?: generateMotivationalMessage(uiState.value.stats)
        setState { copy(motivationalMessage = message) }
        setEffect { StatsContract.Effect.ShowMotivationalMessage(message) }
    }

    private fun generateMotivationalMessage(stats: Stats?): String {
        if (stats == null) return "РќР°С‡РЅРёС‚Рµ СЃРІРѕР№ РїСѓС‚СЊ Рє РѕСЃРѕР·РЅР°РЅРЅРѕСЃС‚Рё СЃРµРіРѕРґРЅСЏ!"

        val totalSessions = stats.completedMeditationSessions + stats.completedBreathingSessions

        return when {
            totalSessions == 0 -> "РќР°С‡РЅРёС‚Рµ СЃРІРѕР№ РїСѓС‚СЊ Рє РѕСЃРѕР·РЅР°РЅРЅРѕСЃС‚Рё СЃРµРіРѕРґРЅСЏ!"
            totalSessions < 5 -> "РћС‚Р»РёС‡РЅРѕРµ РЅР°С‡Р°Р»Рѕ! РљР°Р¶РґС‹Р№ С€Р°Рі РІР°Р¶РµРЅ."
            totalSessions < 10 -> "Р’С‹ РЅР° РїСЂР°РІРёР»СЊРЅРѕРј РїСѓС‚Рё! РџСЂРѕРґРѕР»Р¶Р°Р№С‚Рµ РІ С‚РѕРј Р¶Рµ РґСѓС…Рµ."
            totalSessions < 25 -> "Р’РїРµС‡Р°С‚Р»СЏСЋС‰РёР№ РїСЂРѕРіСЂРµСЃСЃ! Р’С‹ СЃС‚Р°РЅРѕРІРёС‚РµСЃСЊ СЃРёР»СЊРЅРµРµ."
            totalSessions < 50 -> "РќРµРІРµСЂРѕСЏС‚РЅРѕ! Р’С‹ РЅР°СЃС‚РѕСЏС‰РёР№ РјР°СЃС‚РµСЂ РѕСЃРѕР·РЅР°РЅРЅРѕСЃС‚Рё."
            totalSessions < 100 -> "Р›РµРіРµРЅРґР°СЂРЅС‹Р№ СѓСЂРѕРІРµРЅСЊ! Р’С‹ РІРґРѕС…РЅРѕРІР»СЏРµС‚Рµ РґСЂСѓРіРёС…."
            else -> "Р’С‹ РґРѕСЃС‚РёРіР»Рё РїСЂРѕСЃРІРµС‚Р»РµРЅРёСЏ! РџРѕРґРµР»РёС‚РµСЃСЊ РјСѓРґСЂРѕСЃС‚СЊСЋ СЃ РјРёСЂРѕРј."
        }
    }

    override fun onCleared() {
        try {
            // Р”РѕРїРѕР»РЅРёС‚РµР»СЊРЅР°СЏ РѕС‡РёСЃС‚РєР° СЂРµСЃСѓСЂСЃРѕРІ РїСЂРё РЅРµРѕР±С…РѕРґРёРјРѕСЃС‚Рё
            super.onCleared()
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РїСЂРё РѕС‡РёСЃС‚РєРµ StatsViewModel")
        }
    }
}
