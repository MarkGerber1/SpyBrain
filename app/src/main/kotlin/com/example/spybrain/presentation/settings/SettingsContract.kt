package com.example.spybrain.presentation.settings

import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.presentation.navigation.Screen

object SettingsContract {

    data class State(
        val theme: String = "water",
        val ambientEnabled: Boolean = false,
        val ambientTrack: String = "",
        val heartbeatEnabled: Boolean = false,
        val voiceEnabled: Boolean = false,
        val voiceHintsEnabled: Boolean = false,
        val vibrationEnabled: Boolean = true,
        val availableTracks: List<Pair<String, String>> = emptyList(),
        val voiceId: String = "",
        val currentLanguage: String = "ru",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Event : UiEvent {
        data class ThemeSelected(val theme: String) : Event()
        data class AmbientToggled(val enabled: Boolean) : Event()
        data class AmbientTrackSelected(val trackId: String) : Event()
        data class HeartbeatToggled(val enabled: Boolean) : Event()
        data class VoiceToggled(val enabled: Boolean) : Event()
        data class VoiceHintsToggled(val enabled: Boolean) : Event()
        data class VoiceIdSelected(val voiceId: String) : Event()
        data class VibrationToggled(val enabled: Boolean) : Event()
        data class LanguageChanged(val language: String) : Event()
    }

    sealed class Effect : UiEffect {
        data class NavigateTo(val screen: Screen) : Effect()
        data class ShowToast(val message: String) : Effect()
        data class RefreshUI(val language: String) : Effect()
    }
} 