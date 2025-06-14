package com.example.spybrain.presentation.settings

import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.os.VibrationEffect
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
    private val settingsDataStore: SettingsDataStore,
    private val getMeditationsUseCase: GetMeditationsUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel<SettingsContract.Event, SettingsContract.State, SettingsContract.Effect>() {

    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

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
            
        // Подписываемся на вибрацию
        settingsDataStore.vibrationEnabledFlow
            .onEach { setState { copy(vibrationEnabled = it) } }
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
            is SettingsContract.Event.ThemeSelected -> {
                viewModelScope.launch { 
                    settingsDataStore.setTheme(event.theme)
                    setEffect { SettingsContract.Effect.ShowToast("Тема изменена") }
                }
            }
            is SettingsContract.Event.AmbientToggled -> {
                viewModelScope.launch { 
                    val track = settingsDataStore.getAmbientTrack()
                    settingsDataStore.setAmbientEnabled(event.enabled) 
                    handleAmbientMusicChange(event.enabled, track)
                }
            }
            is SettingsContract.Event.AmbientTrackSelected -> {
                viewModelScope.launch { 
                    val enabled = settingsDataStore.getAmbientEnabled()
                    settingsDataStore.setAmbientTrack(event.trackId)
                    if (enabled) {
                        handleAmbientMusicChange(true, event.trackId)
                    }
                }
            }
            is SettingsContract.Event.HeartbeatToggled -> {
                viewModelScope.launch { 
                    settingsDataStore.setHeartbeatEnabled(event.enabled)
                    setEffect { SettingsContract.Effect.ShowToast(if (event.enabled) "Визуализация сердца включена" else "Визуализация сердца выключена") }
                }
            }
            is SettingsContract.Event.VoiceToggled -> {
                viewModelScope.launch { 
                    settingsDataStore.setVoiceEnabled(event.enabled)
                    setEffect { SettingsContract.Effect.ShowToast(if (event.enabled) "Голос включен" else "Голос выключен") }
                }
            }
            is SettingsContract.Event.VoiceHintsToggled -> {
                viewModelScope.launch { 
                    settingsDataStore.setVoiceHintsEnabled(event.enabled)
                    setEffect { SettingsContract.Effect.ShowToast(if (event.enabled) "Голосовые подсказки включены" else "Голосовые подсказки выключены") }
                }
            }
            is SettingsContract.Event.VoiceIdSelected -> {
                viewModelScope.launch { 
                    settingsDataStore.setVoiceId(event.voiceId)
                    setEffect { SettingsContract.Effect.ShowToast("Голос изменен") }
                }
            }
            is SettingsContract.Event.VibrationToggled -> {
                viewModelScope.launch { 
                    settingsDataStore.setVibrationEnabled(event.enabled)
                    if (event.enabled) {
                        // Тестируем вибрацию
                        testVibration()
                    }
                    setEffect { SettingsContract.Effect.ShowToast(if (event.enabled) "Вибрация включена" else "Вибрация выключена") }
                }
            }
            is SettingsContract.Event.LanguageChanged -> {
                viewModelScope.launch {
                    setState { copy(currentLanguage = event.language) }
                    setEffect { SettingsContract.Effect.RefreshUI(event.language) }
                    setEffect { SettingsContract.Effect.ShowToast("Язык изменен на ${if (event.language == "ru") "Русский" else "English"}") }
                }
            }
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
        try {
            // Проверяем, что trackId не пустой
            if (trackId.isEmpty()) {
                setEffect { SettingsContract.Effect.ShowToast("Трек не выбран") }
                return
            }
            
            // Используем существующие аудиофайлы из папки raw
            val audioUrl = when (trackId) {
                "nature" -> "android.resource://${context.packageName}/raw/mixkit_spirit_in_the_woods_139"
                "water" -> "android.resource://${context.packageName}/raw/mixkit_chillax_655"
                "space" -> "android.resource://${context.packageName}/raw/mixkit_staring_at_the_night_sky_168"
                "air" -> "android.resource://${context.packageName}/raw/mixkit_valley_sunset_127"
                else -> {
                    // Если трек не найден, используем дефолтный
                    "android.resource://${context.packageName}/raw/mixkit_relaxation_05_749"
                }
            }
            
            val intent = Intent(context, BackgroundMusicService::class.java).apply {
                action = BackgroundMusicService.ACTION_PLAY
                putExtra(BackgroundMusicService.EXTRA_URL, audioUrl)
            }
            context.startService(intent)
            setEffect { SettingsContract.Effect.ShowToast("Фоновая музыка включена") }
            
        } catch (e: Exception) {
            setEffect { SettingsContract.Effect.ShowToast("Ошибка запуска музыки: ${e.message}") }
        }
    }
    
    // Остановка проигрывания фоновой музыки
    private fun stopAmbientMusic() {
        val intent = Intent(context, BackgroundMusicService::class.java).apply {
            action = BackgroundMusicService.ACTION_STOP
        }
        context.startService(intent)
    }
    
    // Тестирование вибрации
    private fun testVibration() {
        vibrator?.let { v ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(200)
            }
        }
    }
} 