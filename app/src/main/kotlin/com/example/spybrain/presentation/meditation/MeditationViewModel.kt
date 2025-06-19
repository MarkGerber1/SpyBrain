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

@HiltViewModel
class MeditationViewModel @Inject constructor(
    private val getMeditationsUseCase: GetMeditationsUseCase,
    private val trackMeditationSessionUseCase: TrackMeditationSessionUseCase,
    private val playerService: IPlayerService,
    private val aiMentor: IAiMentor,
    private val voiceAssistant: IVoiceAssistant
) : BaseViewModel<MeditationContract.Event, MeditationContract.State, MeditationContract.Effect>() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "Необработанная ошибка в корутине")
        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(exception))
        setEffect { MeditationContract.Effect.ShowError(uiError) }
    }

    private var meditationJob: Job? = null
    private var adviceJob: Job? = null

    val player: IPlayerService
        get() = playerService

    override fun createInitialState(): MeditationContract.State {
        return MeditationContract.State()
    }

    init {
        loadMeditations()
        loadMeditationTracks()
    }

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
                        Timber.e(error, "Ошибка загрузки медитаций")
                        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(error))
                        setState { copy(isLoading = false, error = uiError) }
                        setEffect { MeditationContract.Effect.ShowError(uiError) }
                    }
                    .collect { meditations ->
                        setState { copy(isLoading = false, meditations = meditations) }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Критическая ошибка при загрузке медитаций")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setState { copy(isLoading = false, error = uiError) }
                setEffect { MeditationContract.Effect.ShowError(uiError) }
            }
        }
    }

    private fun loadMeditationTracks() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                // Загружаем треки из репозитория
                val tracks = listOf(
                    MeditationTrack("angelic", R.string.meditation_track_angelic, "audio/meditation_music/meditation_angelic.mp3"),
                    MeditationTrack("chill", R.string.meditation_track_chill, "audio/meditation_music/meditation_chill.mp3"),
                    MeditationTrack("dreaming", R.string.meditation_track_dreaming, "audio/meditation_music/meditation_dreaming.mp3"),
                    MeditationTrack("forest_spirit", R.string.meditation_track_forest_spirit, "audio/meditation_music/meditation_forest_spirit.mp3"),
                    MeditationTrack("night_sky", R.string.meditation_track_night_sky, "audio/meditation_music/meditation_night_sky.mp3"),
                    MeditationTrack("relaxation", R.string.meditation_track_relaxation, "audio/meditation_music/meditation_relaxation.mp3"),
                    MeditationTrack("spiritual", R.string.meditation_track_spiritual, "audio/meditation_music/meditation_spiritual.mp3"),
                    MeditationTrack("valley_sunset", R.string.meditation_track_valley_sunset, "audio/meditation_music/meditation_valley_sunset.mp3")
                )
                
                setState { copy(meditationTracks = tracks) }
                Timber.d("Загружено ${tracks.size} медитационных треков")
            } catch (e: Exception) {
                Timber.e(e, "Ошибка загрузки медитационных треков")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setEffect { MeditationContract.Effect.ShowError(uiError) }
            }
        }
    }

    private fun selectMeditationTrack(track: MeditationTrack) {
        setState { copy(selectedTrack = track) }
        Timber.d("Выбран трек: ${track.id}")
    }

    private fun playSelectedTrack() {
        val currentState = uiState.value
        val track = currentState.selectedTrack ?: return
        
        // Отменяем предыдущие задачи
        meditationJob?.cancel()
        adviceJob?.cancel()
        
        try {
            val audioUrl = "asset:///${track.assetPath}"
            
            // Обновляем UI
            setState { copy(currentPlayingTrack = track, isTrackPlaying = true) }
            
            // Запускаем воспроизведение в отдельной корутине
            meditationJob = viewModelScope.launch(coroutineExceptionHandler + SupervisorJob()) {
                try {
                    playerService.stop()
                    playerService.play(audioUrl)
                    
                    // Проверяем воспроизведение
                    delay(500)
                    if (!playerService.isPlaying()) {
                        setEffect { MeditationContract.Effect.ShowError(UiError.Custom("Не удалось начать воспроизведение трека")) }
                        return@launch
                    }
                    
                    // Запускаем отслеживание прогресса
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
                                
                                delay(100) // Обновляем каждые 100мс для плавности
                            } catch (e: Exception) {
                                Timber.w(e, "Ошибка при отслеживании прогресса")
                                break
                            }
                        }
                    }
                    
                    Timber.d("Воспроизведение трека ${track.id} начато успешно")
                    
                } catch (e: Exception) {
                    Timber.e(e, "Ошибка при воспроизведении трека")
                    val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                    setEffect { MeditationContract.Effect.ShowError(uiError) }
                    setState { copy(currentPlayingTrack = null, isTrackPlaying = false) }
                }
            }
            
        } catch (e: Exception) {
            Timber.e(e, "Критическая ошибка при запуске трека")
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
            Timber.d("Перемотка к позиции: $position")
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при перемотке")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }

    private fun playMeditation(meditation: Meditation) {
        // Отменяем предыдущие задачи
        meditationJob?.cancel()
        adviceJob?.cancel()
        
        try {
            // Проверяем URL на валидность
            if (meditation.audioUrl.isBlank()) {
                setEffect { MeditationContract.Effect.ShowError(UiError.Custom("URL аудио отсутствует")) }
                return
            }
            
            // Нормализуем URL для медитаций
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
            
            // Обновляем UI
            setState { copy(currentPlaying = meditation) }
            
            // Запускаем воспроизведение в отдельной корутине
            meditationJob = viewModelScope.launch(coroutineExceptionHandler + SupervisorJob()) {
                try {
                    playerService.stop()
                    playerService.play(audioUrl)
                    
                    // AI наставник: вступительный совет
                    try {
                        aiMentor.giveMeditationAdvice()
                    } catch (e: Exception) {
                        Timber.w(e, "Ошибка при получении совета от AI наставника")
                        // Не прерываем воспроизведение из-за ошибки AI
                    }
                    
                    // Проверяем воспроизведение
                    delay(500)
                    if (!playerService.isPlaying()) {
                        setEffect { MeditationContract.Effect.ShowError(UiError.Custom("Не удалось начать воспроизведение")) }
                        return@launch
                    }
                    
                    // Запускаем периодические советы в отдельной корутине
                    adviceJob = launch(coroutineExceptionHandler + SupervisorJob()) {
                        try {
                            while (isActive && playerService.isPlaying()) {
                                delay(60000L)
                                if (isActive) {
                                    try {
                                        aiMentor.giveMeditationAdvice()
                                    } catch (e: Exception) {
                                        Timber.w(e, "Ошибка при получении периодического совета")
                                        // Продолжаем цикл
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Ошибка в цикле советов")
                        }
                    }
                    
                } catch (e: Exception) {
                    Timber.e(e, "Ошибка при воспроизведении медитации")
                    val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                    setEffect { MeditationContract.Effect.ShowError(uiError) }
                    setState { copy(currentPlaying = null) }
                }
            }
            
            // Голосовой анонс
            setEffect { MeditationContract.Effect.Speak(meditation.title) }
            
        } catch (e: Exception) {
            Timber.e(e, "Критическая ошибка при запуске медитации")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }

    private fun pauseMeditation() {
        try {
            playerService.pause()
            adviceJob?.cancel()
            
            // Обновляем состояние для треков
            if (uiState.value.currentPlayingTrack != null) {
                setState { copy(isTrackPlaying = false) }
            }
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при паузе медитации")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }

    private fun stopMeditation() {
        try {
            meditationJob?.cancel()
            adviceJob?.cancel()
            playerService.stop()
            
            // Сбрасываем состояние для треков
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
            Timber.e(e, "Ошибка при остановке медитации")
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
                Timber.e(e, "Ошибка при отслеживании сессии")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setEffect { MeditationContract.Effect.ShowError(uiError) }
            }
        }
    }

    fun startListeningVoice() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                // TODO: Реализовать запуск прослушивания
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(Exception("Голосовой ввод пока не реализован")))
                setEffect { MeditationContract.Effect.ShowError(uiError) }
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при запуске голосового ввода")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setEffect { MeditationContract.Effect.ShowError(uiError) }
            }
        }
    }

    private fun handleVoiceCommand(command: String) {
        try {
            setEffect { MeditationContract.Effect.Speak("Голосовая команда: $command") }
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при обработке голосовой команды")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }
    
    fun cleanupResources() {
        try {
            meditationJob?.cancel()
            adviceJob?.cancel()
            
            uiState.value.currentPlaying?.let {
                playerService.pause()
            }
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при очистке ресурсов")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }
} 