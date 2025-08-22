package com.example.spybrain.presentation.settings

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
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
import com.example.spybrain.R
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEffect

/**
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val getMeditationsUseCase: GetMeditationsUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel<SettingsContract.Event, SettingsContract.State, SettingsContract.Effect>() {

    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    override fun createInitialState(): SettingsContract.State = SettingsContract.State()

    init {
        // Подписываемся на поток темы
        settingsDataStore.themeFlow
            .onEach { setState { copy(theme = it) } }
            .launchIn(viewModelScope)

        // Имя/возраст/пол
        settingsDataStore.userNameFlow
            .onEach { setState { copy(userName = it) } }
            .launchIn(viewModelScope)
        settingsDataStore.userAgeFlow
            .onEach { setState { copy(userAge = it?.toString() ?: "") } }
            .launchIn(viewModelScope)
        settingsDataStore.userGenderFlow
            .onEach { setState { copy(userGender = it ?: "") } }
            .launchIn(viewModelScope)

        // Подписываемся на включение фоновой музыки
        settingsDataStore.ambientEnabledFlow
            .onEach { enabled ->
                setState { copy(ambientEnabled = enabled) }

                // Автоматически запускаем музыку при включении
                if (enabled) {
                    settingsDataStore.ambientTrackFlow.map { track ->
                        handleAmbientMusicChange(true, track)
                    }.launchIn(viewModelScope)
                } else {
                    stopAmbientMusic()
                }
            }
            .launchIn(viewModelScope)

        // Подписываемся на выбор трека
        settingsDataStore.ambientTrackFlow
            .onEach { track ->
                setState { copy(ambientTrack = track) }

                // Автоматически переключаем трек если музыка включена
                settingsDataStore.ambientEnabledFlow.map { enabled ->
                    if (enabled) {
                        handleAmbientMusicChange(true, track)
                    }
                }.launchIn(viewModelScope)
            }
            .launchIn(viewModelScope)

        // Подписка на визуализацию сердца
        settingsDataStore.heartbeatEnabledFlow
            .onEach { setState { copy(heartbeatEnabled = it) } }
            .launchIn(viewModelScope)

        // Подписка на голосовые подсказки
        settingsDataStore.voiceEnabledFlow
            .onEach { setState { copy(voiceEnabled = it) } }
            .launchIn(viewModelScope)

        settingsDataStore.voiceHintsEnabledFlow
            .onEach { setState { copy(voiceHintsEnabled = it) } }
            .launchIn(viewModelScope)

        // Подписка на вибрацию
        settingsDataStore.vibrationEnabledFlow
            .onEach { setState { copy(vibrationEnabled = it) } }
            .launchIn(viewModelScope)

        // Подписка на доступные треки медитации
        getMeditationsUseCase()
            .map { meditations -> meditations.map { it.id to it.title } }
            .onEach { setState { copy(availableTracks = it) } }
            .launchIn(viewModelScope)

        settingsDataStore.voiceIdFlow
            .onEach { setState { copy(voiceId = it) } }
            .launchIn(viewModelScope)

        // Автоматически запускаем фоновую музыку при старте приложения (если включена)
        viewModelScope.launch {
            val isEnabled = settingsDataStore.getAmbientEnabled()
            val track = settingsDataStore.getAmbientTrack()
            if (isEnabled && track.isNotEmpty()) {
                handleAmbientMusicChange(true, track)
            }
        }
    }

    override fun handleEvent(event: SettingsContract.Event) {
        when (event) {
            is SettingsContract.Event.ThemeSelected -> {
                viewModelScope.launch {
                    settingsDataStore.setTheme(event.theme)
                    setEffect { SettingsContract.Effect.ShowToast(context.getString(R.string.toast_theme_changed)) }
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
                    setEffect {
                        SettingsContract.Effect.ShowToast(
                            if (event.enabled)
                                context.getString(R.string.toast_heartbeat_on)
                            else
                                context.getString(R.string.toast_heartbeat_off)
                        )
                    }
                }
            }
            is SettingsContract.Event.VoiceToggled -> {
                viewModelScope.launch {
                    settingsDataStore.setVoiceEnabled(event.enabled)
                    setEffect {
                        SettingsContract.Effect.ShowToast(
                            if (event.enabled)
                                context.getString(R.string.toast_voice_on)
                            else
                                context.getString(R.string.toast_voice_off)
                        )
                    }
                }
            }
            is SettingsContract.Event.VoiceHintsToggled -> {
                viewModelScope.launch {
                    settingsDataStore.setVoiceHintsEnabled(event.enabled)
                    setEffect {
                        SettingsContract.Effect.ShowToast(
                            if (event.enabled)
                                context.getString(R.string.toast_voice_hints_on)
                            else
                                context.getString(R.string.toast_voice_hints_off)
                        )
                    }
                }
            }
            is SettingsContract.Event.VoiceIdSelected -> {
                viewModelScope.launch {
                    settingsDataStore.setVoiceId(event.voiceId)
                    setEffect { SettingsContract.Effect.ShowToast(context.getString(R.string.toast_voice_changed)) }
                }
            }
            is SettingsContract.Event.VibrationToggled -> {
                viewModelScope.launch {
                    settingsDataStore.setVibrationEnabled(event.enabled)
                    if (event.enabled) {
                        // Тестируем вибрацию
                        testVibration()
                    }
                    setEffect {
                        SettingsContract.Effect.ShowToast(
                            if (event.enabled)
                                context.getString(R.string.toast_vibration_on)
                            else
                                context.getString(R.string.toast_vibration_off)
                        )
                    }
                }
            }
            is SettingsContract.Event.LanguageChanged -> {
                viewModelScope.launch {
                    setState { copy(currentLanguage = event.language) }
                    setEffect { SettingsContract.Effect.RefreshUI(event.language) }
                    setEffect {
                        SettingsContract.Effect.ShowToast(
                            context.getString(
                                R.string.toast_language_changed,
                                if (event.language == "ru") "Русский" else "English"
                            )
                        )
                    }
                }
            }
            is SettingsContract.Event.UserNameChanged -> setState { copy(userName = event.name) }
            is SettingsContract.Event.UserAgeChanged -> setState { copy(userAge = event.age.filter { it.isDigit() }.take(3)) }
            is SettingsContract.Event.UserGenderChanged -> setState { copy(userGender = event.gender) }
            SettingsContract.Event.SaveProfile -> {
                viewModelScope.launch {
                    val name = uiState.value.userName.trim()
                    val age = uiState.value.userAge.trim().toIntOrNull()
                    val gender = uiState.value.userGender.trim().ifEmpty { null }
                    if (name.isNotEmpty()) settingsDataStore.setUserName(name)
                    settingsDataStore.setUserAge(age)
                    settingsDataStore.setUserGender(gender)
                    setEffect { SettingsContract.Effect.ShowToast(context.getString(R.string.toast_profile_saved)) }
                }
            }
        }
    }

    private fun handleAmbientMusicChange(enabled: Boolean, trackId: String) {
        if (enabled && trackId.isNotEmpty()) {
            playAmbientMusic(trackId)
        } else {
            stopAmbientMusic()
        }
    }

    private fun playAmbientMusic(trackId: String) {
        try {
            if (trackId.isEmpty()) {
                setEffect { SettingsContract.Effect.ShowToast(context.getString(R.string.toast_track_not_selected)) }
                return
            }

            val audioUrl = when (trackId) {
                "nature" -> "android.resource://${context.packageName}/raw/mixkit_spirit_in_the_woods_139"
                "water" -> "android.resource://${context.packageName}/raw/mixkit_chillax_655"
                "space" -> "android.resource://${context.packageName}/raw/mixkit_staring_at_the_night_sky_168"
                "air" -> "android.resource://${context.packageName}/raw/mixkit_valley_sunset_127"
                else -> {
                    "android.resource://${context.packageName}/raw/mixkit_relaxation_05_749"
                }
            }

            val intent = Intent(context, BackgroundMusicService::class.java).apply {
                action = BackgroundMusicService.ACTION_PLAY
                putExtra(BackgroundMusicService.EXTRA_URL, audioUrl)
            }
            context.startService(intent)
            setEffect { SettingsContract.Effect.ShowToast(context.getString(R.string.toast_ambient_on)) }

        } catch (e: Exception) {
            setEffect { SettingsContract.Effect.ShowToast(context.getString(R.string.toast_ambient_error, e.message ?: "")) }
        }
    }

    private fun stopAmbientMusic() {
        val intent = Intent(context, BackgroundMusicService::class.java).apply {
            action = BackgroundMusicService.ACTION_STOP
        }
        context.startService(intent)
    }

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
