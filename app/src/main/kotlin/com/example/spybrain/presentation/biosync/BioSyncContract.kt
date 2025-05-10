package com.example.spybrain.presentation.biosync

import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.util.UiError

object BioSyncContract {
    data class State(
        val bpm: Int = 0,
        val error: UiError? = null
    ) : UiState

    sealed class Event : UiEvent {
        object StartSync : Event()
        object StopSync : Event()
    }

    sealed class Effect : UiEffect {
        data class ShowError(val error: UiError) : Effect()
    }
} 