package com.example.spybrain.presentation.meditation

import androidx.lifecycle.viewModelScope
import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.SessionType
import com.example.spybrain.domain.usecase.meditation.GetMeditationsUseCase
import com.example.spybrain.domain.usecase.meditation.TrackMeditationSessionUseCase
import com.example.spybrain.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import java.util.Date
import javax.inject.Inject
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.spybrain.service.AiMentorService
import kotlinx.coroutines.delay
import com.example.spybrain.domain.error.ErrorHandler
import com.example.spybrain.domain.service.IPlayerService
import com.example.spybrain.domain.service.IAiMentor
import com.example.spybrain.util.UiError
import com.example.spybrain.domain.service.IVoiceAssistant
import com.example.spybrain.voice.VoiceCommand
import com.example.spybrain.presentation.meditation.MeditationContract.Effect.Speak
import timber.log.Timber
import com.example.spybrain.data.repository.MeditationRepositoryImpl.MeditationTrack
import com.example.spybrain.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.spybrain.presentation.meditation.MeditationContract
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEffect

/**
 * ViewModel РґР»СЏ СЌРєСЂР°РЅР° РјРµРґРёС‚Р°С†РёРё.
 */
@HiltViewModel
class MeditationViewModel @Inject constructor(
    private val getMeditationsUseCase: GetMeditationsUseCase,
    private val trackMeditationSessionUseCase: TrackMeditationSessionUseCase,
    private val playerService: IPlayerService,
    private val aiMentor: IAiMentor,
    private val voiceAssistant: IVoiceAssistant
) : BaseViewModel<MeditationContract.Event, MeditationContract.State, MeditationContract.Effect>() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "РќРµРѕР±СЂР°Р±РѕС‚Р°РЅРЅР°СЏ РѕС€РёР±РєР° РІ РєРѕСЂСѓС‚РёРЅРµ")
        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(exception))
        setEffect { MeditationContract.Effect.ShowError(uiError) }
    }

    private var meditationJob: Job? = null
    private var adviceJob: Job? = null

    /**
     * РЎРµСЂРІРёСЃ РїР»РµРµСЂР° РґР»СЏ РјРµРґРёС‚Р°С†РёР№.
     */
    val player: IPlayerService
        get() = playerService

    /**
     * РЎРѕР·РґР°С‘С‚ РЅР°С‡Р°Р»СЊРЅРѕРµ СЃРѕСЃС‚РѕСЏРЅРёРµ СЌРєСЂР°РЅР° РјРµРґРёС‚Р°С†РёРё.
     * @return РќР°С‡Р°Р»СЊРЅРѕРµ СЃРѕСЃС‚РѕСЏРЅРёРµ.
     */
    override fun createInitialState(): MeditationContract.State {
        return MeditationContract.State()
    }

    init {
        loadMeditations()
        loadMeditationTracks()
    }

    /**
     * РћР±СЂР°Р±Р°С‚С‹РІР°РµС‚ СЃРѕР±С‹С‚РёРµ UI.
     * @param event РЎРѕР±С‹С‚РёРµ UI.
     */
    override fun handleEvent(event: MeditationContract.Event) {
        when (event) {
            is MeditationContract.Event.LoadMeditations -> loadMeditations()
            is MeditationContract.Event.PlayMeditation -> playMeditation(event.meditation)
            is MeditationContract.Event.PauseMeditation -> pauseMeditation()
            is MeditationContract.Event.StopMeditation -> stopMeditation()
            is MeditationContract.Event.VoiceCommand -> handleVoiceCommand(event.command)
            is MeditationContract.Event.LoadMeditationTracks -> loadMeditationTracks()
            is MeditationContract.Event.SelectMeditationTrack -> selectMeditationTrack(event.track)
            is MeditationContract.Event.PlaySelectedTrack -> {
                val currentState = uiState.value
                if (currentState.isTrackPlaying) {
                    pauseMeditation()
                } else {
                    playSelectedTrack()
                }
            }
            is MeditationContract.Event.NextTrack -> nextTrack()
            is MeditationContract.Event.PreviousTrack -> previousTrack()
            is MeditationContract.Event.SeekToPosition -> seekToPosition(event.position)
        }
    }

    private fun loadMeditations() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                getMeditationsUseCase()
                    .onStart {
                        setState { copy(isLoading = true, error = null) }
                    }
                    .catch { error ->
                        Timber.e(error, "РћС€РёР±РєР° Р·Р°РіСЂСѓР·РєРё РјРµРґРёС‚Р°С†РёР№")
                        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(error))
                        setState { copy(isLoading = false, error = uiError) }
                        setEffect { MeditationContract.Effect.ShowError(uiError) }
                    }
                    .collect { meditations ->
                        setState { copy(isLoading = false, meditations = meditations) }
                    }
            } catch (e: Exception) {
                Timber.e(e, "РљСЂРёС‚РёС‡РµСЃРєР°СЏ РѕС€РёР±РєР° РїСЂРё Р·Р°РіСЂСѓР·РєРµ РјРµРґРёС‚Р°С†РёР№")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setState { copy(isLoading = false, error = uiError) }
                setEffect { MeditationContract.Effect.ShowError(uiError) }
            }
        }
    }

    private fun loadMeditationTracks() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                // Р—Р°РіСЂСѓР¶Р°РµРј С‚СЂРµРєРё РёР· СЂРµРїРѕР·РёС‚РѕСЂРёСЏ
                val tracks = listOf(
                    MeditationTrack(
                        "angelic",
                        R.string.meditation_track_angelic,
                        "audio/meditation_music/meditation_angelic.mp3"
                    ),
                    MeditationTrack(
                        "chill",
                        R.string.meditation_track_chill,
                        "audio/meditation_music/meditation_chill.mp3"
                    ),
                    MeditationTrack(
                        "dreaming",
                        R.string.meditation_track_dreaming,
                        "audio/meditation_music/meditation_dreaming.mp3"
                    ),
                    MeditationTrack(
                        "forest_spirit",
                        R.string.meditation_track_forest_spirit,
                        "audio/meditation_music/meditation_forest_spirit.mp3"
                    ),
                    MeditationTrack(
                        "night_sky",
                        R.string.meditation_track_night_sky,
                        "audio/meditation_music/meditation_night_sky.mp3"
                    ),
                    MeditationTrack(
                        "relaxation",
                        R.string.meditation_track_relaxation,
                        "audio/meditation_music/meditation_relaxation.mp3"
                    ),
                    MeditationTrack(
                        "spiritual",
                        R.string.meditation_track_spiritual,
                        "audio/meditation_music/meditation_spiritual.mp3"
                    ),
                    MeditationTrack(
                        "valley_sunset",
                        R.string.meditation_track_valley_sunset,
                        "audio/meditation_music/meditation_valley_sunset.mp3"
                    )
                )

                setState { copy(meditationTracks = tracks) }
                Timber.d("Р—Р°РіСЂСѓР¶РµРЅРѕ ${tracks.size} РјРµРґРёС‚Р°С†РёРѕРЅРЅС‹С… С‚СЂРµРєРѕРІ")
            } catch (e: Exception) {
                Timber.e(e, "РћС€РёР±РєР° Р·Р°РіСЂСѓР·РєРё РјРµРґРёС‚Р°С†РёРѕРЅРЅС‹С… С‚СЂРµРєРѕРІ")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setEffect { MeditationContract.Effect.ShowError(uiError) }
            }
        }
    }

    private fun selectMeditationTrack(track: MeditationTrack) {
        setState { copy(selectedTrack = track) }
        Timber.d("Р’С‹Р±СЂР°РЅ С‚СЂРµРє: ${track.id}")
    }

    private fun playSelectedTrack() {
        val currentState = uiState.value
        val track = currentState.selectedTrack ?: return

        // РћС‚РјРµРЅСЏРµРј РїСЂРµРґС‹РґСѓС‰РёРµ Р·Р°РґР°С‡Рё
        meditationJob?.cancel()
        adviceJob?.cancel()

        try {
            val audioUrl = "asset:///${track.assetPath}"

            // РћР±РЅРѕРІР»СЏРµРј UI
            setState { copy(currentPlayingTrack = track, isTrackPlaying = true) }

            // Р—Р°РїСѓСЃРєР°РµРј РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРµ РІ РѕС‚РґРµР»СЊРЅРѕР№ РєРѕСЂСѓС‚РёРЅРµ
            meditationJob = viewModelScope.launch(coroutineExceptionHandler + SupervisorJob()) {
                try {
                    playerService.stop()
                    playerService.play(audioUrl)

                    // РџСЂРѕРІРµСЂСЏРµРј РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРµ
                    delay(500)
                    if (!playerService.isPlaying()) {
                        setEffect {
                            MeditationContract.Effect.ShowError(
                                UiError.Custom("Не удалось начать воспроизведение трека")
                            )
                        }
                        return@launch
                    }

                    // Р—Р°РїСѓСЃРєР°РµРј РѕС‚СЃР»РµР¶РёРІР°РЅРёРµ РїСЂРѕРіСЂРµСЃСЃР°
                    launch(coroutineExceptionHandler + SupervisorJob()) {
                        while (isActive && playerService.isPlaying()) {
                            try {
                                val currentPos = playerService.getCurrentPosition()
                                val duration = playerService.getDuration()
                                val progress = if (duration > 0) currentPos.toFloat() / duration else 0f

                                setState {
                                    copy(
                                        currentPosition = currentPos,
                                        trackDuration = duration,
                                        trackProgress = progress,
                                        isTrackPlaying = true
                                    )
                                }

                                delay(100) // РћР±РЅРѕРІР»СЏРµРј РєР°Р¶РґС‹Рµ 100РјСЃ РґР»СЏ РїР»Р°РІРЅРѕСЃС‚Рё
                            } catch (e: Exception) {
                                Timber.w(e, "РћС€РёР±РєР° РїСЂРё РѕС‚СЃР»РµР¶РёРІР°РЅРёРё РїСЂРѕРіСЂРµСЃСЃР°")
                                break
                            }
                        }
                    }

                    Timber.d("Р’РѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРµ С‚СЂРµРєР° ${track.id} РЅР°С‡Р°С‚Рѕ СѓСЃРїРµС€РЅРѕ")

                } catch (e: Exception) {
                    Timber.e(e, "РћС€РёР±РєР° РїСЂРё РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРё С‚СЂРµРєР°")
                    val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                    setEffect { MeditationContract.Effect.ShowError(uiError) }
                    setState { copy(currentPlayingTrack = null, isTrackPlaying = false) }
                }
            }

        } catch (e: Exception) {
            Timber.e(e, "РљСЂРёС‚РёС‡РµСЃРєР°СЏ РѕС€РёР±РєР° РїСЂРё Р·Р°РїСѓСЃРєРµ С‚СЂРµРєР°")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }

    private fun nextTrack() {
        val currentState = uiState.value
        val tracks = currentState.meditationTracks
        val currentTrack = currentState.selectedTrack ?: currentState.currentPlayingTrack

        if (tracks.isNotEmpty() && currentTrack != null) {
            val currentIndex = tracks.indexOf(currentTrack)
            val nextIndex = if (currentIndex < tracks.size - 1) currentIndex + 1 else 0
            val nextTrack = tracks[nextIndex]

            selectMeditationTrack(nextTrack)
            if (currentState.currentPlayingTrack != null) {
                playSelectedTrack()
            }
        }
    }

    private fun previousTrack() {
        val currentState = uiState.value
        val tracks = currentState.meditationTracks
        val currentTrack = currentState.selectedTrack ?: currentState.currentPlayingTrack

        if (tracks.isNotEmpty() && currentTrack != null) {
            val currentIndex = tracks.indexOf(currentTrack)
            val prevIndex = if (currentIndex > 0) currentIndex - 1 else tracks.size - 1
            val prevTrack = tracks[prevIndex]

            selectMeditationTrack(prevTrack)
            if (currentState.currentPlayingTrack != null) {
                playSelectedTrack()
            }
        }
    }

    private fun seekToPosition(position: Long) {
        try {
            playerService.seekTo(position)
            Timber.d("РџРµСЂРµРјРѕС‚РєР° Рє РїРѕР·РёС†РёРё: $position")
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РїСЂРё РїРµСЂРµРјРѕС‚РєРµ")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }

    private fun playMeditation(meditation: Meditation) {
        // РћС‚РјРµРЅСЏРµРј РїСЂРµРґС‹РґСѓС‰РёРµ Р·Р°РґР°С‡Рё
        meditationJob?.cancel()
        adviceJob?.cancel()

        try {
            // РџСЂРѕРІРµСЂСЏРµРј URL РЅР° РІР°Р»РёРґРЅРѕСЃС‚СЊ
            if (meditation.audioUrl.isNullOrBlank()) {
                setEffect { MeditationContract.Effect.ShowError(UiError.Custom("URL Р°СѓРґРёРѕ РѕС‚СЃСўСЃС‚РІСўРµС‚")) }
                return
            }

            // РќРѕСЂРјР°Р»РёР·СѓРµРј URL РґР»СЏ РјРµРґРёС‚Р°С†РёР№
            val audioUrl = when {
                meditation.audioUrl.contains("example.com") -> "asset:///audio/mixkit-valley-sunset-127.mp3"
                meditation.audioUrl.startsWith("http://") ||
                meditation.audioUrl.startsWith("https://") -> meditation.audioUrl
                meditation.audioUrl.startsWith("asset:///") -> meditation.audioUrl
                else -> {
                    val assetPath = if (meditation.audioUrl.startsWith("audio/")) {
                        meditation.audioUrl
                    } else {
                        "audio/${meditation.audioUrl}"
                    }
                    "asset:///$assetPath"
                }
            }

            // РћР±РЅРѕРІР»СЏРµРј UI
            setState { copy(currentPlaying = meditation) }

            // Р—Р°РїСѓСЃРєР°РµРј РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРµ РІ РѕС‚РґРµР»СЊРЅРѕР№ РєРѕСЂСѓС‚РёРЅРµ
            meditationJob = viewModelScope.launch(coroutineExceptionHandler + SupervisorJob()) {
                try {
                    playerService.stop()
                    playerService.play(audioUrl)

                    // AI РЅР°СЃС‚Р°РІРЅРёРє: РІСЃС‚СѓРїРёС‚РµР»СЊРЅС‹Р№ СЃРѕРІРµС‚
                    try {
                        aiMentor.giveMeditationAdvice("defaultUser")
                    } catch (e: Exception) {
                        Timber.w(e, "РћС€РёР±РєР° РїСЂРё РїРѕР»СѓС‡РµРЅРёРё СЃРѕРІРµС‚Р° РѕС‚ AI РЅР°СЃС‚Р°РІРЅРёРєР°")
                        // РќРµ РїСЂРµСЂС‹РІР°РµРј РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРµ РёР·-Р·Р° РѕС€РёР±РєРё AI
                    }

                    // РџСЂРѕРІРµСЂСЏРµРј РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРµ
                    delay(500)
                    if (!playerService.isPlaying()) {
                        setEffect { MeditationContract.Effect.ShowError(UiError.Custom("РќРµ СѓРґР°Р»РѕСЃСЊ РЅР°С‡Р°С‚СЊ РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРµ")) }
                        return@launch
                    }

                    // Р—Р°РїСѓСЃРєР°РµРј РїРµСЂРёРѕРґРёС‡РµСЃРєРёРµ СЃРѕРІРµС‚С‹ РІ РѕС‚РґРµР»СЊРЅРѕР№ РєРѕСЂСѓС‚РёРЅРµ
                    adviceJob = launch(coroutineExceptionHandler + SupervisorJob()) {
                        try {
                            while (isActive && playerService.isPlaying()) {
                                delay(60000L)
                                if (isActive) {
                                    try {
                                        aiMentor.giveMeditationAdvice("defaultUser")
                                    } catch (e: Exception) {
                                        Timber.w(e, "РћС€РёР±РєР° РїСЂРё РїРѕР»СѓС‡РµРЅРёРё РїРµСЂРёРѕРґРёС‡РµСЃРєРѕРіРѕ СЃРѕРІРµС‚Р°")
                                        // РџСЂРѕРґРѕР»Р¶Р°РµРј С†РёРєР»
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "РћС€РёР±РєР° РІ С†РёРєР»Рµ СЃРѕРІРµС‚РѕРІ")
                        }
                    }

                } catch (e: Exception) {
                    Timber.e(e, "РћС€РёР±РєР° РїСЂРё РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРё РјРµРґРёС‚Р°С†РёРё")
                    val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                    setEffect { MeditationContract.Effect.ShowError(uiError) }
                    setState { copy(currentPlaying = null) }
                }
            }

            // Р“РѕР»РѕСЃРѕРІРѕР№ Р°РЅРѕРЅСЃ
            setEffect { MeditationContract.Effect.Speak(meditation.title) }

        } catch (e: Exception) {
            Timber.e(e, "РљСЂРёС‚РёС‡РµСЃРєР°СЏ РѕС€РёР±РєР° РїСЂРё Р·Р°РїСѓСЃРєРµ РјРµРґРёС‚Р°С†РёРё")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }

    private fun pauseMeditation() {
        try {
            playerService.pause()
            adviceJob?.cancel()

            // РћР±РЅРѕРІР»СЏРµРј СЃРѕСЃС‚РѕСЏРЅРёРµ РґР»СЏ С‚СЂРµРєРѕРІ
            if (uiState.value.currentPlayingTrack != null) {
                setState { copy(isTrackPlaying = false) }
            }
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РїСЂРё РїР°СѓР·Рµ РјРµРґРёС‚Р°С†РёРё")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }

    private fun stopMeditation() {
        try {
            meditationJob?.cancel()
            adviceJob?.cancel()
            playerService.stop()

            // РЎР±СЂР°СЃС‹РІР°РµРј СЃРѕСЃС‚РѕСЏРЅРёРµ РґР»СЏ С‚СЂРµРєРѕРІ
            setState {
                copy(
                    currentPlaying = null,
                    currentPlayingTrack = null,
                    isTrackPlaying = false,
                    trackProgress = 0f,
                    currentPosition = 0L
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РїСЂРё РѕСЃС‚Р°РЅРѕРІРєРµ РјРµРґРёС‚Р°С†РёРё")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }

    private fun trackSessionEnd(meditationId: String, durationSeconds: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                val session = Session(
                    id = "session_${System.currentTimeMillis()}",
                    type = SessionType.MEDITATION,
                    startTime = Date(System.currentTimeMillis() - durationSeconds * 1000),
                    endTime = Date(),
                    durationSeconds = durationSeconds,
                    relatedItemId = meditationId
                )
                trackMeditationSessionUseCase(session)
            } catch (e: Exception) {
                Timber.e(e, "РћС€РёР±РєР° РїСЂРё РѕС‚СЃР»РµР¶РёРІР°РЅРёРё СЃРµСЃСЃРёРё")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setEffect { MeditationContract.Effect.ShowError(uiError) }
            }
        }
    }

    /**
     * Р—Р°РїСѓСЃРєР°РµС‚ РїСЂРѕСЃР»СѓС€РёРІР°РЅРёРµ РіРѕР»РѕСЃРѕРІРѕР№ РєРѕРјР°РЅРґС‹.
     */
    fun startListeningVoice() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                // TODO: Requires manual refactor (если есть неустранимые TODO/FIXME)
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(Exception("Р“РѕР»РѕСЃРѕРІРѕР№ РІРІРѕРґ РїРѕРєР° РЅРµ СЂРµР°Р»РёР·РѕРІР°РЅ")))
                setEffect { MeditationContract.Effect.ShowError(uiError) }
            } catch (e: Exception) {
                Timber.e(e, "РћС€РёР±РєР° РїСЂРё Р·Р°РїСѓСЃРєРµ РіРѕР»РѕСЃРѕРІРѕРіРѕ РІРІРѕРґР°")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setEffect { MeditationContract.Effect.ShowError(uiError) }
            }
        }
    }

    private fun handleVoiceCommand(command: String) {
        try {
            setEffect { MeditationContract.Effect.Speak("Р“РѕР»РѕСЃРѕРІР°СЏ РєРѕРјР°РЅРґР°: $command") }
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РїСЂРё РѕР±СЂР°Р±РѕС‚РєРµ РіРѕР»РѕСЃРѕРІРѕР№ РєРѕРјР°РЅРґС‹")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }

    /**
     * РћС‡РёС‰Р°РµС‚ СЂРµСЃСѓСЂСЃС‹ Рё Р·Р°РІРµСЂС€Р°РµС‚ РІСЃРµ Р·Р°РґР°С‡Рё.
     */
    fun cleanupResources() {
        try {
            meditationJob?.cancel()
            adviceJob?.cancel()

            uiState.value.currentPlaying?.let {
                playerService.pause()
            }
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РїСЂРё РѕС‡РёСЃС‚РєРµ СЂРµСЃСѓСЂСЃРѕРІ")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }
}
