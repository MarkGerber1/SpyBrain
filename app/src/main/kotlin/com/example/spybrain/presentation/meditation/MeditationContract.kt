package com.example.spybrain.presentation.meditation

import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.util.UiError
import com.example.spybrain.data.repository.MeditationRepositoryImpl.MeditationTrack

object MeditationContract {
    data class State(
        val isLoading: Boolean = false,
        val meditations: List<Meditation> = emptyList(),
        val currentPlaying: Meditation? = null,
        val meditationTracks: List<MeditationTrack> = emptyList(),
        val selectedTrack: MeditationTrack? = null,
        val currentPlayingTrack: MeditationTrack? = null,
        val isTrackPlaying: Boolean = false,
        val trackProgress: Float = 0f,
        val trackDuration: Long = 0L,
        val currentPosition: Long = 0L,
        val error: UiError? = null // TODO реализовано: централизованная обработка ошибок
    ) : UiState

    sealed class Event : UiEvent {
        object LoadMeditations : Event()
        data class PlayMeditation(val meditation: Meditation) : Event()
        object PauseMeditation : Event()
        object StopMeditation : Event()
        data class VoiceCommand(val command: String) : Event()
        
        // События для медитационных треков
        object LoadMeditationTracks : Event()
        data class SelectMeditationTrack(val track: MeditationTrack) : Event()
        object PlaySelectedTrack : Event()
        object NextTrack : Event()
        object PreviousTrack : Event()
        data class SeekToPosition(val position: Long) : Event()
    }

    sealed class Effect : UiEffect {
        data class ShowError(val error: UiError) : Effect()
        // Effects for player controls, navigation etc.
        data class Speak(val text: String) : Effect()
        data class TrackStarted(val track: MeditationTrack) : Effect()
        data class TrackCompleted(val track: MeditationTrack) : Effect()
    }
} 