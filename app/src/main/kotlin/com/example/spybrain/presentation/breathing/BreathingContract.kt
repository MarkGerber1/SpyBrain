package com.example.spybrain.presentation.breathing

import com.example.spybrain.domain.model.BreathingPattern
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.util.UiError

object BreathingContract {
    data class State(
        val isLoading: Boolean = false,
        val patterns: List<BreathingPattern> = emptyList(),
        val currentPattern: BreathingPattern? = null,
        val currentPhase: BreathingPhase = BreathingPhase.Idle,
        val cycleProgress: Float = 0f, // 0.0 to 1.0 within a phase
        val remainingCycles: Int = 0,
        val error: UiError? = null // TODO реализовано: централизованная обработка ошибок
    ) : UiState

    sealed class Event : UiEvent {
        object LoadPatterns : Event()
        data class StartPattern(val pattern: BreathingPattern) : Event()
        object StopPattern : Event()
        data class VoiceCommand(val text: String) : Event()
        // Internal events can be added for timer ticks if needed
    }

    sealed class Effect : UiEffect {
        data class ShowError(val error: UiError) : Effect()
        object Vibrate : Effect() // Example effect for phase change
        data class Speak(val text: String) : Effect() // Голосовая подсказка
    }

    enum class BreathingPhase {
        Idle, Inhale, HoldAfterInhale, Exhale, HoldAfterExhale
    }
} 