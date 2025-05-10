package com.example.spybrain.presentation.meditation

import com.example.spybrain.domain.model.MeditationProgram
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.util.UiError

object MeditationLibraryContract {
    data class State(
        val programs: List<MeditationProgram> = emptyList(),
        val error: UiError? = null
    ) : UiState

    sealed class Event : UiEvent {
        object LoadPrograms : Event()
    }

    sealed class Effect : UiEffect {
        data class ShowError(val error: UiError) : Effect()
    }
} 