package com.example.spybrain.presentation.stats

import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.Stats
import com.example.spybrain.domain.model.BreathingSession
import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.util.UiError

/**
 */
interface StatsContract {
    data class State(
        val isLoading: Boolean = false,
        val stats: Stats? = null,
        val sessionHistory: List<Session> = emptyList(),
        val breathingHistory: List<BreathingSession> = emptyList(),
        val motivationalMessage: String = "",
        val error: UiError? = null
    ) : UiState

    sealed class Event : UiEvent {
        /**
         */
        object LoadStatsAndHistory : Event()
        /**
         */
        object RefreshStats : Event()
        /**
         */
        data class ShowMotivationalMessage(val message: String) : Event()
    }

    sealed class Effect : UiEffect {
        /**
         */
        data class ShowError(val error: UiError) : Effect()
        /**
         */
        data class ShowMotivationalMessage(val message: String) : Effect()
        /**
         */
        data class ShowAchievements(val achievements: List<Achievement>) : Effect()
    }
}
