package com.example.spybrain.presentation.breathing.patternbuilder

import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect

object BreathingPatternBuilderContract {
    data class State(
        val id: String? = null,
        val patterns: List<CustomBreathingPattern> = emptyList(),
        val name: String = "",
        val description: String = "",
        val inhaleSeconds: String = "",
        val holdAfterInhaleSeconds: String = "",
        val exhaleSeconds: String = "",
        val holdAfterExhaleSeconds: String = "",
        val totalCycles: String = ""
    ) : UiState

    sealed class Event : UiEvent {
        /** Загрузить существующий шаблон по ID */
        data class LoadPattern(val id: String) : Event()
        data class EnterName(val name: String) : Event()
        data class EnterDescription(val description: String) : Event()
        data class EnterInhale(val seconds: String) : Event()
        data class EnterHoldInhale(val seconds: String) : Event()
        data class EnterExhale(val seconds: String) : Event()
        data class EnterHoldExhale(val seconds: String) : Event()
        data class EnterCycles(val cycles: String) : Event()
        object SavePattern : Event()
        data class DeletePattern(val pattern: CustomBreathingPattern) : Event()
    }

    sealed class Effect : UiEffect {
        data class ShowError(val error: com.example.spybrain.util.UiError) : Effect()
        data class ShowSuccessMessage(val message: String) : Effect()
    }
} 