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
            playerService.stop()
            playerService.play(meditation.audioUrl)
            setState { copy(currentPlaying = meditation) }
            // AI наставник: вступительный совет
            aiMentor.giveMeditationAdvice()
            // периодические советы каждые 60 секунд
            viewModelScope.launch {
                while (playerService.isPlaying()) {
                    delay(60000L)
                    aiMentor.giveMeditationAdvice()
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
} 