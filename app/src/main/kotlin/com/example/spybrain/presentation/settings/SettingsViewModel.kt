package com.example.spybrain.presentation.settings

import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.example.spybrain.data.datastore.SettingsDataStore
import com.example.spybrain.domain.usecase.meditation.GetMeditationsUseCase
import com.example.spybrain.presentation.base.BaseViewModel
import com.example.spybrain.service.BackgroundMusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore, // TODO: Рассмотреть доступ к SettingsDataStore через репозиторий и UseCase для соответствия Clean Architecture
    private val getMeditationsUseCase: GetMeditationsUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel<SettingsContract.Event, SettingsContract.State, SettingsContract.Effect>() { // TODO: Добавить все необходимые юнит-тесты для ViewModel

    override fun createInitialState(): SettingsContract.State = SettingsContract.State()

    init {
        // Подписываемся на поток темы
        settingsDataStore.themeFlow
            .onEach { setState { copy(theme = it) } }
            .launchIn(viewModelScope)
        // Подписываемся на включение фоновой музыки
        settingsDataStore.ambientEnabledFlow
            .onEach { enabled -> 
                setState { copy(ambientEnabled = enabled) }
                
                // Используем актуальное значение после setState
                settingsDataStore.ambientTrackFlow.map { track ->
                    handleAmbientMusicChange(enabled, track)
                }.launchIn(viewModelScope)
            }
            .launchIn(viewModelScope)
        // Подписываемся на выбор трека
        settingsDataStore.ambientTrackFlow
            .onEach { track -> 
                setState { copy(ambientTrack = track) }
                
                // Используем актуальное значение после setState
                settingsDataStore.ambientEnabledFlow.map { enabled ->
                    if (enabled) {
                        handleAmbientMusicChange(true, track)
                    }
                }.launchIn(viewModelScope)
            }
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
                viewModelScope.launch { 
                    val track = settingsDataStore.getAmbientTrack() // Получаем синхронно текущий трек
                    settingsDataStore.setAmbientEnabled(event.enabled) 
                    handleAmbientMusicChange(event.enabled, track)
                }
            is SettingsContract.Event.AmbientTrackSelected ->
                viewModelScope.launch { 
                    val enabled = settingsDataStore.getAmbientEnabled() // Получаем синхронно текущее состояние
                    settingsDataStore.setAmbientTrack(event.trackId)
                    if (enabled) {
                        handleAmbientMusicChange(true, event.trackId)
                    }
                }
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
    
    // Обработка изменений настроек фоновой музыки
    private fun handleAmbientMusicChange(enabled: Boolean, trackId: String) {
        if (enabled && trackId.isNotEmpty()) {
            playAmbientMusic(trackId)
        } else {
            stopAmbientMusic()
        }
    }
    
    // Запуск сервиса проигрывания фоновой музыки
    private fun playAmbientMusic(trackId: String) {
        val audioUrl = "https://example.com/audio/$trackId.mp3" // Заменить на реальный URL из медитации
        val intent = Intent(context, BackgroundMusicService::class.java).apply {
            action = BackgroundMusicService.ACTION_PLAY
            putExtra(BackgroundMusicService.EXTRA_URL, audioUrl)
        }
        context.startService(intent)
        setEffect { SettingsContract.Effect.ShowToast("Фоновая музыка включена") }
    }
    
    // Остановка проигрывания фоновой музыки
    private fun stopAmbientMusic() {
        val intent = Intent(context, BackgroundMusicService::class.java).apply {
            action = BackgroundMusicService.ACTION_STOP
        }
        context.startService(intent)
    }
} 