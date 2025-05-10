package com.example.spybrain.presentation.meditation

import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.util.UiError

object MeditationContract {
    data class State(
        val isLoading: Boolean = false,
        val meditations: List<Meditation> = emptyList(),
        val currentPlaying: Meditation? = null,
        val error: UiError? = null // TODO реализовано: централизованная обработка ошибок
    ) : UiState

    sealed class Event : UiEvent {
        object LoadMeditations : Event()
        data class PlayMeditation(val meditation: Meditation) : Event()
        object PauseMeditation : Event()
        object StopMeditation : Event()
        data class VoiceCommand(val command: String) : Event()
    }

    sealed class Effect : UiEffect {
        data class ShowError(val error: UiError) : Effect()
        // Effects for player controls, navigation etc.
        data class Speak(val text: String) : Effect()
    }
} 