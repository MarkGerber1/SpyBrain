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
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.spybrain.R
import timber.log.Timber
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
        // РџРѕРґРїРёСЃС‹РІР°РµРјСЃСЏ РЅР° РїРѕС‚РѕРє С‚РµРјС‹
        settingsDataStore.themeFlow
            .onEach { setState { copy(theme = it) } }
            .launchIn(viewModelScope)

        // Подписываемся на включение фоновой музыки (без автозапуска при старте)
        var isFirstAmbientLoad = true
        settingsDataStore.ambientEnabledFlow
            .onEach { enabled ->
                setState { copy(ambientEnabled = enabled) }
                // Запускаем музыку только при явном изменении настройки, не при загрузке
                if (!isFirstAmbientLoad) {
                    val currentTrack = settingsDataStore.getAmbientTrack()
                    handleAmbientMusicChange(enabled, currentTrack)
                } else {
                    isFirstAmbientLoad = false
                    // При первой загрузке запускаем музыку только если она включена
                    if (enabled) {
                        val currentTrack = settingsDataStore.getAmbientTrack()
                        handleAmbientMusicChange(enabled, currentTrack, showToast = false)
                    }
                }
            }
            .launchIn(viewModelScope)

        // Подписка на выбор трека (без автопереключений/автопуска)
        settingsDataStore.ambientTrackFlow
            .onEach { track ->
                setState { copy(ambientTrack = track) }
            }
            .launchIn(viewModelScope)

        // Подписка на громкость ambient
        settingsDataStore.ambientVolumeFlow
            .onEach { volume ->
                setState { copy(ambientVolume = volume) }
                // Пробрасываем громкость в сервис
                try {
                    val intent = Intent(context, com.example.spybrain.service.AmbientMusicService::class.java).apply {
                        action = com.example.spybrain.service.AmbientMusicService.ACTION_SET_VOLUME
                        putExtra(com.example.spybrain.service.AmbientMusicService.EXTRA_VOLUME, volume)
                    }
                    context.startService(intent)
                } catch (e: Exception) {
                    Timber.w(e, "Cannot start ambient music service in background")
                }
            }
            .launchIn(viewModelScope)

        // РџРѕРґРїРёСЃС‹РІР°РµРјСЃСЏ РЅР° РІРёР·СѓР°Р»РёР·Р°С†РёСЋ СЃРµСЂРґС†Р°
        settingsDataStore.heartbeatEnabledFlow
            .onEach { setState { copy(heartbeatEnabled = it) } }
            .launchIn(viewModelScope)

        // РџРѕРґРїРёСЃС‹РІР°РµРјСЃСЏ РЅР° РіРѕР»РѕСЃРѕРІС‹Рµ РїРѕРґСЃРєР°Р·РєРё
        settingsDataStore.voiceEnabledFlow
            .onEach { setState { copy(voiceEnabled = it) } }
            .launchIn(viewModelScope)

        // РџРѕРґРїРёСЃС‹РІР°РµРјСЃСЏ РЅР° РіРѕР»РѕСЃРѕРІС‹Рµ РїРѕРґСЃРєР°Р·РєРё (РЅРѕРІРѕРµ РїРѕР»Рµ)
        settingsDataStore.voiceHintsEnabledFlow
            .onEach { setState { copy(voiceHintsEnabled = it) } }
            .launchIn(viewModelScope)

        // РџРѕРґРїРёСЃС‹РІР°РµРјСЃСЏ РЅР° РІРёР±СЂР°С†РёСЋ
        settingsDataStore.vibrationEnabledFlow
            .onEach { setState { copy(vibrationEnabled = it) } }
            .launchIn(viewModelScope)

        // Профиль пользователя
        settingsDataStore.userNameFlow
            .onEach { setState { copy(userName = it) } }
            .launchIn(viewModelScope)
        settingsDataStore.userAgeFlow
            .onEach { setState { copy(userAge = it) } }
            .launchIn(viewModelScope)
        settingsDataStore.userGenderFlow
            .onEach { setState { copy(userGender = it) } }
            .launchIn(viewModelScope)

        settingsDataStore.backgroundStyleFlow
            .onEach { setState { copy(backgroundStyle = it) } }
            .launchIn(viewModelScope)

        // Подписываемся на видео-фоны
        settingsDataStore.videoBackgroundsEnabledFlow
            .onEach { setState { copy(videoBackgroundsEnabled = it) } }
            .launchIn(viewModelScope)

        // Подписываемся на анимированные фоны
        settingsDataStore.animatedBackgroundsEnabledFlow
            .onEach { setState { copy(animatedBackgroundsEnabled = it) } }
            .launchIn(viewModelScope)

        // Предлагаем список поддерживаемых ambient-треков (а не список медитаций)
        val ambientOptions = listOf(
            "nature" to "Природа",
            "water" to "Вода/Океан",
            "space" to "Космос",
            "air" to "Воздух",
            "relax" to "Релакс"
        )
        setState { copy(availableTracks = ambientOptions) }

        settingsDataStore.voiceIdFlow
            .onEach { setState { copy(voiceId = it) } }
            .launchIn(viewModelScope)

        settingsDataStore.voiceRateFlow
            .onEach { setState { copy(voiceRate = it) } }
            .launchIn(viewModelScope)

        settingsDataStore.voicePitchFlow
            .onEach { setState { copy(voicePitch = it) } }
            .launchIn(viewModelScope)

        // Больше не автозапускаем при старте приложения — только по явному действию пользователя
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
                    var track = settingsDataStore.getAmbientTrack()
                    settingsDataStore.setAmbientEnabled(event.enabled)
                    if (event.enabled && track.isEmpty()) {
                        // Назначаем дефолтный трек, если ранее не был выбран
                        track = "nature"
                        settingsDataStore.setAmbientTrack(track)
                    }
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
            is SettingsContract.Event.AmbientVolumeChanged -> {
                viewModelScope.launch {
                    // Храним проценты, но приходят float 0..1
                    settingsDataStore.setAmbientVolumePercent((event.volume * 100).toInt())
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
            is SettingsContract.Event.VoiceRateChanged -> {
                viewModelScope.launch {
                    settingsDataStore.setVoiceRatePercent((event.rate * 100).toInt())
                }
            }
            is SettingsContract.Event.VoicePitchChanged -> {
                viewModelScope.launch {
                    settingsDataStore.setVoicePitchPercent((event.pitch * 100).toInt())
                }
            }
            is SettingsContract.Event.VibrationToggled -> {
                viewModelScope.launch {
                    settingsDataStore.setVibrationEnabled(event.enabled)
                    if (event.enabled) {
                        // РўРµСЃС‚РёСЂСѓРµРј РІРёР±СЂР°С†РёСЋ
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
                    // Применяем локаль на лету
                    LocaleManager.setLocale(event.language)
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
            is SettingsContract.Event.UserNameChanged -> {
                viewModelScope.launch { settingsDataStore.setUserName(event.name) }
            }
            is SettingsContract.Event.UserAgeChanged -> {
                viewModelScope.launch { settingsDataStore.setUserAge(event.age) }
            }
            is SettingsContract.Event.UserGenderChanged -> {
                viewModelScope.launch { settingsDataStore.setUserGender(event.gender) }
            }
            is SettingsContract.Event.BackgroundStyleChanged -> {
                viewModelScope.launch { settingsDataStore.setBackgroundStyle(event.style) }
            }
            is SettingsContract.Event.VideoBackgroundsToggled -> {
                viewModelScope.launch {
                    settingsDataStore.setVideoBackgroundsEnabled(event.enabled)
                    setEffect {
                        SettingsContract.Effect.ShowToast(
                            if (event.enabled)
                                "Включены видео-фоны"
                            else
                                "Включены Canvas-анимации"
                        )
                    }
                }
            }
            is SettingsContract.Event.AnimatedBackgroundsToggled -> {
                viewModelScope.launch {
                    settingsDataStore.setAnimatedBackgroundsEnabled(event.enabled)
                    setEffect {
                        SettingsContract.Effect.ShowToast(
                            if (event.enabled)
                                "Включены живые фоны"
                            else
                                "Включены статичные фоны"
                        )
                    }
                }
            }
        }
    }

    // РћР±СЂР°Р±РѕС‚РєР° РёР·РјРµРЅРµРЅРёР№ РЅР°СЃС‚СЂРѕРµРє С„РѕРЅРѕРІРѕР№ РјСѓР·С‹РєРё
    private fun handleAmbientMusicChange(enabled: Boolean, trackId: String, showToast: Boolean = true) {
        if (enabled && trackId.isNotEmpty()) {
            playAmbientMusic(trackId, showToast)
        } else {
            stopAmbientMusic(showToast)
        }
    }

    // Р—Р°РїСѓСЃРє СЃРµСЂРІРёСЃР° РїСЂРѕРёРіСЂС‹РІР°РЅРёСЏ С„РѕРЅРѕРІРѕР№ РјСѓР·С‹РєРё
    private fun playAmbientMusic(trackId: String, showToast: Boolean = true) {
        try {
            // РџСЂРѕРІРµСЂСЏРµРј, С‡С‚Рѕ trackId РЅРµ РїСѓСЃС‚РѕР№
            if (trackId.isEmpty()) {
                setEffect { SettingsContract.Effect.ShowToast(context.getString(R.string.toast_track_not_selected)) }
                return
            }

            // РСЃРїРѕР»СЊР·СѓРµРј СЃСѓС‰РµСЃС‚РІСѓСЋС‰РёРµ Р°СѓРґРёРѕС„Р°Р№Р»С‹ РёР· РїР°РїРєРё raw
            val audioUrl = when (trackId) {
                "nature" -> "android.resource://${context.packageName}/raw/mixkit_spirit_in_the_woods_139"
                "water" -> "android.resource://${context.packageName}/raw/mixkit_chillax_655"
                "space" -> "android.resource://${context.packageName}/raw/mixkit_staring_at_the_night_sky_168"
                "air" -> "android.resource://${context.packageName}/raw/mixkit_valley_sunset_127"
                else -> {
                    // Р•СЃР»Рё С‚СЂРµРє РЅРµ РЅР°Р№РґРµРЅ, РёСЃРїРѕР»СЊР·СѓРµРј РґРµС„РѕР»С‚РЅС‹Р№
                    "android.resource://${context.packageName}/raw/mixkit_relaxation_05_749"
                }
            }

            // Переключено на AmbientMusicService с трек-id и управлением громкостью
            val intent = Intent(context, com.example.spybrain.service.AmbientMusicService::class.java).apply {
                action = com.example.spybrain.service.AmbientMusicService.ACTION_PLAY
                putExtra(com.example.spybrain.service.AmbientMusicService.EXTRA_TRACK_ID, trackId)
            }
            context.startService(intent)
            if (showToast) {
                setEffect { SettingsContract.Effect.ShowToast(context.getString(R.string.toast_ambient_on)) }
            }

        } catch (e: Exception) {
            setEffect { SettingsContract.Effect.ShowToast(context.getString(R.string.toast_ambient_error, e.message ?: "")) }
        }
    }

    // РћСЃС‚Р°РЅРѕРІРєР° РїСЂРѕРёРіСЂС‹РІР°РЅРёСЏ С„РѕРЅРѕРІРѕР№ РјСѓР·С‹РєРё
    private fun stopAmbientMusic(showToast: Boolean = true) {
        try {
            val intent = Intent(context, com.example.spybrain.service.AmbientMusicService::class.java).apply {
                action = com.example.spybrain.service.AmbientMusicService.ACTION_STOP
            }
            context.startService(intent)
            if (showToast) {
                setEffect { SettingsContract.Effect.ShowToast("Фоновая музыка остановлена") }
            }
        } catch (e: Exception) {
            Timber.w(e, "Cannot stop ambient music service")
        }
    }

    // РўРµСЃС‚РёСЂРѕРІР°РЅРёРµ РІРёР±СЂР°С†РёРё
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
