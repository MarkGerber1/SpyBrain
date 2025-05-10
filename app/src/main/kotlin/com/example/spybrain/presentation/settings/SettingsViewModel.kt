package com.example.spybrain.presentation.settings

import androidx.lifecycle.viewModelScope
import com.example.spybrain.data.datastore.SettingsDataStore
import com.example.spybrain.domain.usecase.meditation.GetMeditationsUseCase
import com.example.spybrain.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore, // TODO: Рассмотреть доступ к SettingsDataStore через репозиторий и UseCase для соответствия Clean Architecture
    private val getMeditationsUseCase: GetMeditationsUseCase
) : BaseViewModel<SettingsContract.Event, SettingsContract.State, SettingsContract.Effect>() { // TODO: Добавить все необходимые юнит-тесты для ViewModel

    override fun createInitialState(): SettingsContract.State = SettingsContract.State()

    init {
        // Подписываемся на поток темы
        settingsDataStore.themeFlow
            .onEach { setState { copy(theme = it) } }
            .launchIn(viewModelScope)
        // Подписываемся на включение фоновой музыки
        settingsDataStore.ambientEnabledFlow
            .onEach { setState { copy(ambientEnabled = it) } }
            .launchIn(viewModelScope)
        // Подписываемся на выбор трека
        settingsDataStore.ambientTrackFlow
            .onEach { setState { copy(ambientTrack = it) } }
            .launchIn(viewModelScope)
        // Подписываемся на визуализацию сердца
        settingsDataStore.heartbeatEnabledFlow
            .onEach { setState { copy(heartbeatEnabled = it) } }
            .launchIn(viewModelScope)
        // Подписываемся на голосовые подсказки
        settingsDataStore.voiceEnabledFlow
            .onEach { setState { copy(voiceEnabled = it) } }
            .launchIn(viewModelScope)
        // Подписываемся на голосовые подсказки (новое поле)
        settingsDataStore.voiceHintsEnabledFlow
            .onEach { setState { copy(voiceHintsEnabled = it) } }
            .launchIn(viewModelScope)
        // Подписываемся на доступные треки медитации
        getMeditationsUseCase()
            .map { meditations -> meditations.map { it.id to it.title } }
            .onEach { setState { copy(availableTracks = it) } }
            .launchIn(viewModelScope)
        settingsDataStore.voiceIdFlow
            .onEach { setState { copy(voiceId = it) } }
            .launchIn(viewModelScope)
    }

    override fun handleEvent(event: SettingsContract.Event) {
        when (event) {
            is SettingsContract.Event.ThemeSelected ->
                viewModelScope.launch { settingsDataStore.setTheme(event.theme) }
            is SettingsContract.Event.AmbientToggled ->
                viewModelScope.launch { settingsDataStore.setAmbientEnabled(event.enabled) }
            is SettingsContract.Event.AmbientTrackSelected ->
                viewModelScope.launch { settingsDataStore.setAmbientTrack(event.trackId) }
            is SettingsContract.Event.HeartbeatToggled ->
                viewModelScope.launch { settingsDataStore.setHeartbeatEnabled(event.enabled) }
            is SettingsContract.Event.VoiceToggled ->
                viewModelScope.launch { settingsDataStore.setVoiceEnabled(event.enabled) }
            is SettingsContract.Event.VoiceHintsToggled ->
                viewModelScope.launch { settingsDataStore.setVoiceHintsEnabled(event.enabled) }
            is SettingsContract.Event.VoiceIdSelected ->
                viewModelScope.launch { settingsDataStore.setVoiceId(event.voiceId) }
        }
    }
} 