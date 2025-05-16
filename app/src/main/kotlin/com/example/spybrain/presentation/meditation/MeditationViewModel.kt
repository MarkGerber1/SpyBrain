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
import java.util.Date
import javax.inject.Inject
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.spybrain.voice.AiMentorService
import kotlinx.coroutines.delay
import com.example.spybrain.domain.error.ErrorHandler
import com.example.spybrain.domain.service.IPlayerService
import com.example.spybrain.domain.service.IAiMentor
import com.example.spybrain.util.UiError
import com.example.spybrain.domain.service.IVoiceAssistant
import com.example.spybrain.voice.VoiceCommand
import com.example.spybrain.presentation.meditation.MeditationContract.Effect.Speak // FIXME билд-фикс 09.05.2025

@HiltViewModel
class MeditationViewModel @Inject constructor(
    private val getMeditationsUseCase: GetMeditationsUseCase,
    private val trackMeditationSessionUseCase: TrackMeditationSessionUseCase,
    private val playerService: IPlayerService, // TODO реализовано: внедрение через абстракцию
    private val aiMentor: IAiMentor, // TODO реализовано: внедрение через абстракцию
    private val voiceAssistant: IVoiceAssistant // TODO реализовано: внедрение через абстракцию
) : BaseViewModel<MeditationContract.Event, MeditationContract.State, MeditationContract.Effect>() { // TODO: Добавить все необходимые юнит-тесты для ViewModel

    val player: IPlayerService
        get() = playerService

    override fun createInitialState(): MeditationContract.State {
        return MeditationContract.State()
    }

    init {
        loadMeditations()
    }

    override fun handleEvent(event: MeditationContract.Event) {
        when (event) {
            is MeditationContract.Event.LoadMeditations -> loadMeditations()
            is MeditationContract.Event.PlayMeditation -> playMeditation(event.meditation)
            is MeditationContract.Event.PauseMeditation -> pauseMeditation()
            is MeditationContract.Event.StopMeditation -> stopMeditation()
            is MeditationContract.Event.VoiceCommand -> handleVoiceCommand(event.command)
        }
    }

    private fun loadMeditations() {
        viewModelScope.launch {
            getMeditationsUseCase()
                .onStart { setState { copy(isLoading = true, error = null) } }
                .catch { error ->
                    val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(error))
                    setState { copy(isLoading = false, error = uiError) }
                    setEffect { MeditationContract.Effect.ShowError(uiError) }
                }
                .collect { meditations ->
                    setState { copy(isLoading = false, meditations = meditations) }
                }
        }
    }

    private fun playMeditation(meditation: Meditation) {
        // Проверяем, не нулевой ли плеер
        if (playerService == null) {
            setEffect { MeditationContract.Effect.ShowError(UiError.Custom("Сервис плеера недоступен")) }
            return
        }
        
        try {
            // Проверяем URL на валидность
            if (meditation.audioUrl.isBlank()) {
                setEffect { MeditationContract.Effect.ShowError(UiError.Custom("URL аудио отсутствует")) }
                return
            }
            
            // Нормализуем URL для медитаций - если URL не содержит протокол, 
            // или если это пример URL, заменяем на локальный ассет
            val audioUrl = when {
                meditation.audioUrl.contains("example.com") -> "file:///android_asset/audio/mixkit-valley-sunset-127.mp3"
                meditation.audioUrl.startsWith("http://") || 
                meditation.audioUrl.startsWith("https://") -> meditation.audioUrl
                else -> {
                    // Предполагаем, что это либо имя файла, либо относительный путь к ассету
                    val assetPath = if (meditation.audioUrl.startsWith("audio/")) {
                        meditation.audioUrl
                    } else {
                        "audio/${meditation.audioUrl}"
                    }
                    
                    // Используем формат, который точно работает с ExoPlayer
                    "file:///android_asset/$assetPath"
                }
            }
            
            // Сначала обновляем UI, чтобы показать воспроизведение
            setState { copy(currentPlaying = meditation) }
            
            // Используем безопасное воспроизведение
            viewModelScope.launch {
                try {
                    playerService.stop()
                    playerService.play(audioUrl)
                    
                    // AI наставник: вступительный совет
                    aiMentor.giveMeditationAdvice()
                    
                    // Проверяем, начало ли воспроизведение, иначе показываем ошибку
                    delay(500) // Небольшая задержка для инициализации
                    if (!playerService.isPlaying()) {
                        // Если воспроизведение не началось, возможно, была ошибка
                        setEffect { MeditationContract.Effect.ShowError(UiError.Custom("Не удалось начать воспроизведение")) }
                    } else {
                        // Периодические советы каждые 60 секунд
                        while (playerService.isPlaying()) {
                            delay(60000L)
                            aiMentor.giveMeditationAdvice()
                        }
                    }
                } catch (e: Exception) {
                    val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                    setEffect { MeditationContract.Effect.ShowError(uiError) }
                }
            }
            
            // Голосовой анонс начала медитации
            setEffect { MeditationContract.Effect.Speak(meditation.title) }
        } catch (e: Exception) {
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }

    private fun pauseMeditation() {
        playerService.pause()
    }

    private fun stopMeditation() {
        playerService.stop()
        setState { copy(currentPlaying = null) }
        // TODO реализовано: Голосовой анонс окончания через ViewModel
        setEffect { MeditationContract.Effect.Speak("Медитация завершена") } // Пример анонса // TODO: Вынести строку в strings.xml
    }

    private fun trackSessionEnd(meditationId: String, durationSeconds: Long) {
        viewModelScope.launch {
            try {
                val session = Session(
                    id = "session_${System.currentTimeMillis()}", // Generate unique ID
                    type = SessionType.MEDITATION,
                    startTime = Date(System.currentTimeMillis() - durationSeconds * 1000),
                    endTime = Date(),
                    durationSeconds = durationSeconds,
                    relatedItemId = meditationId
                )
                trackMeditationSessionUseCase(session)
            } catch (e: Exception) {
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setEffect { MeditationContract.Effect.ShowError(uiError) }
            }
        }
    }

    // Метод для запуска прослушивания голосовой команды
    fun startListeningVoice() {
        // TODO: Реализовать запуск прослушивания через VoiceAssistant и передачу результата в handleEvent
        // voiceAssistant.startListening { command -> handleEvent(MeditationContract.Event.VoiceCommand(command)) }
        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(Exception("Голосовой ввод пока не реализован"))) // TODO: Вынести строку в strings.xml, возможно маппинг через ErrorHandler потребуется доработать для UI ошибок заглушек
        setEffect { MeditationContract.Effect.ShowError(uiError) } // TODO: Вынести строку в strings.xml
    }

    // FIXME тип-фикс 09.05.2025: исправлены возвращаемые значения на MeditationContract.Effect
    private fun handleVoiceCommand(command: String) {
        // TODO: Реализовать обработку голосовых команд для медитации
        setEffect { MeditationContract.Effect.Speak("Голосовая команда: $command") } // FIXME билд-фикс 09.05.2025
    }
    
    // Метод для очистки ресурсов при размонтировании экрана
    fun cleanupResources() {
        try {
            // Используем uiState.value для доступа к текущему состоянию
            uiState.value.currentPlaying?.let {
                playerService.pause()
            }
            // Освобождаем только если выходим со страницы, но не полностью (для продолжения в фоне)
            // Полная очистка происходит в onDestroy сервиса
        } catch (e: Exception) {
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { MeditationContract.Effect.ShowError(uiError) }
        }
    }
} 