package com.example.spybrain.presentation.breathing

import androidx.lifecycle.viewModelScope
import com.example.spybrain.domain.model.BreathingPattern
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.SessionType
import com.example.spybrain.data.repository.BreathingPatternRepository
import com.example.spybrain.domain.usecase.breathing.TrackBreathingSessionUseCase
import com.example.spybrain.domain.usecase.stats.SaveSessionUseCase
import com.example.spybrain.presentation.base.BaseViewModel
import com.example.spybrain.service.VoiceAssistantService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.util.Date
import javax.inject.Inject
import com.example.spybrain.presentation.breathing.BreathingContract.Effect.Speak
import java.util.Locale
import com.example.spybrain.util.UiError
import com.example.spybrain.domain.error.ErrorHandler
import timber.log.Timber

@HiltViewModel
class BreathingViewModel @Inject constructor(
    private val breathingPatternRepository: BreathingPatternRepository,
    private val trackBreathingSessionUseCase: TrackBreathingSessionUseCase,
    private val saveSessionUseCase: SaveSessionUseCase,
    private val voiceAssistant: VoiceAssistantService
) : BaseViewModel<BreathingContract.Event, BreathingContract.State, BreathingContract.Effect>() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "Необработанная ошибка в корутине дыхания")
        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(exception))
        setEffect { BreathingContract.Effect.ShowError(uiError) }
    }

    private var breathingJob: Job? = null
    private var sessionStartTime: Long = 0

    override fun createInitialState(): BreathingContract.State {
        return BreathingContract.State()
    }

    init {
        loadPatterns()
    }

    override fun handleEvent(event: BreathingContract.Event) {
        when (event) {
            is BreathingContract.Event.LoadPatterns -> loadPatterns()
            is BreathingContract.Event.StartPattern -> startPattern(event.pattern)
            is BreathingContract.Event.StopPattern -> stopPattern()
            is BreathingContract.Event.VoiceCommand -> {
                try {
                    val command = event.text.lowercase(Locale.getDefault())
                    when {
                        command.contains("стоп") || command.contains("stop") -> stopPattern()
                        command.contains("начать") || command.contains("start") || command.contains("вдох") -> {
                            uiState.value.patterns.firstOrNull()?.let { startPattern(it) }
                                ?: setEffect { BreathingContract.Effect.ShowError(UiError.Custom("Нет доступных шаблонов")) }
                        }
                        else -> setEffect { BreathingContract.Effect.Speak("Команда не распознана") }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Ошибка при обработке голосовой команды")
                    val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                    setEffect { BreathingContract.Effect.ShowError(uiError) }
                }
            }
        }
    }

    private fun loadPatterns() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                setState { copy(isLoading = true, error = null) }
                
                val patterns = breathingPatternRepository.getAllPatterns()
                setState { copy(isLoading = false, patterns = patterns) }
                
                Timber.d("Loaded ${patterns.size} breathing patterns")
                
            } catch (e: Exception) {
                Timber.e(e, "Критическая ошибка при загрузке паттернов")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setState { copy(isLoading = false, error = uiError) }
                setEffect { BreathingContract.Effect.ShowError(uiError) }
            }
        }
    }

    private fun startPattern(pattern: BreathingPattern) {
        try {
            stopPattern() // Stop any existing pattern
            sessionStartTime = System.currentTimeMillis()
            setState {
                copy(
                    currentPattern = pattern,
                    remainingCycles = pattern.totalCycles,
                    currentPhase = BreathingContract.BreathingPhase.Idle
                )
            }
            
            // Голосовое сопровождение старта
            if (voiceAssistant.isReady()) {
                voiceAssistant.speakStart()
                voiceAssistant.speakBreathingPrompt(pattern.voicePrompt)
            } else {
                setEffect { Speak("Начинаем. Вдох...") }
            }
            
            breathingJob = viewModelScope.launch(coroutineExceptionHandler + SupervisorJob()) {
                runBreathingCycle(pattern)
            }
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при запуске паттерна дыхания")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { BreathingContract.Effect.ShowError(uiError) }
        }
    }

    private suspend fun runBreathingCycle(pattern: BreathingPattern) {
        val currentJob = breathingJob ?: return
        try {
            while (currentJob.isActive && uiState.value.remainingCycles > 0) {
                val currentCycle = pattern.totalCycles - uiState.value.remainingCycles + 1
                
                // Inhale
                if (voiceAssistant.isReady()) {
                    voiceAssistant.speakInhale()
                } else {
                    setEffect { Speak("Вдох") }
                }
                
                if (!runPhase(BreathingContract.BreathingPhase.Inhale, pattern.inhaleSeconds, currentJob)) break
                
                // Hold After Inhale
                if (pattern.holdAfterInhaleSeconds > 0) {
                    if (voiceAssistant.isReady()) {
                        voiceAssistant.speakHold()
                    } else {
                        setEffect { Speak("Задержка") }
                    }
                    if (!runPhase(BreathingContract.BreathingPhase.HoldAfterInhale, pattern.holdAfterInhaleSeconds, currentJob)) break
                }
                
                // Exhale
                if (voiceAssistant.isReady()) {
                    voiceAssistant.speakExhale()
                } else {
                    setEffect { Speak("Выдох") }
                }
                if (!runPhase(BreathingContract.BreathingPhase.Exhale, pattern.exhaleSeconds, currentJob)) break
                
                // Hold After Exhale
                if (pattern.holdAfterExhaleSeconds > 0) {
                    if (voiceAssistant.isReady()) {
                        voiceAssistant.speakRelax()
                    } else {
                        setEffect { Speak("Отдых") }
                    }
                    if (!runPhase(BreathingContract.BreathingPhase.HoldAfterExhale, pattern.holdAfterExhaleSeconds, currentJob)) break
                }

                setState { copy(remainingCycles = remainingCycles - 1) }
                
                // Мотивация каждые 3 цикла
                if (currentCycle % 3 == 0 && voiceAssistant.isReady()) {
                    voiceAssistant.speakMotivation()
                }
            }
            
            // Cycle finished or stopped
            if (currentJob.isActive) {
                trackSessionEnd(pattern.id, System.currentTimeMillis() - sessionStartTime)
                
                if (voiceAssistant.isReady()) {
                    voiceAssistant.speakComplete()
                } else {
                    setEffect { Speak("Отлично! Вы завершили дыхательную практику.") }
                }
                
                stopPatternInternal()
            }
        } catch (e: Exception) {
            Timber.e(e, "Ошибка в цикле дыхания")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { BreathingContract.Effect.ShowError(uiError) }
            stopPatternInternal()
        }
    }

    private suspend fun runPhase(phase: BreathingContract.BreathingPhase, durationSeconds: Int, job: Job): Boolean {
        if (durationSeconds <= 0) return true
        
        try {
            setState { copy(currentPhase = phase, cycleProgress = 0f) }
            setEffect { BreathingContract.Effect.Vibrate }
            val stepMillis = 100L
            val totalSteps = (durationSeconds * 1000 / stepMillis).toInt()

            for (step in 1..totalSteps) {
                if (!job.isActive) return false
                delay(stepMillis)
                setState { copy(cycleProgress = step.toFloat() / totalSteps) }
            }
            setState { copy(cycleProgress = 1f) }
            return job.isActive
        } catch (e: Exception) {
            Timber.e(e, "Ошибка в фазе дыхания: $phase")
            return false
        }
    }

    private fun stopPattern() {
        try {
            val pattern = uiState.value.currentPattern
            val startTime = sessionStartTime
            stopPatternInternal()
            pattern?.let {
                val durationMillis = System.currentTimeMillis() - startTime
                if (durationMillis > 1000) {
                    trackSessionEnd(it.id, durationMillis)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при остановке паттерна")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { BreathingContract.Effect.ShowError(uiError) }
        }
    }

    private fun stopPatternInternal() {
        try {
            breathingJob?.cancel()
            breathingJob = null
            setState { 
                copy(
                    currentPattern = null, 
                    currentPhase = BreathingContract.BreathingPhase.Idle, 
                    remainingCycles = 0, 
                    cycleProgress = 0f
                ) 
            }
            sessionStartTime = 0
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при внутренней остановке паттерна")
        }
    }

    private fun trackSessionEnd(patternId: String, durationMillis: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                val session = Session(
                    id = "session_${System.currentTimeMillis()}",
                    type = SessionType.BREATHING,
                    startTime = Date(sessionStartTime),
                    endTime = Date(),
                    durationSeconds = durationMillis / 1000,
                    relatedItemId = patternId
                )
                saveSessionUseCase(session)
                trackBreathingSessionUseCase(session)
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при отслеживании сессии дыхания")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setEffect { BreathingContract.Effect.ShowError(uiError) }
            }
        }
    }

    override fun onCleared() {
        try {
            stopPatternInternal()
            super.onCleared()
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при очистке ViewModel")
        }
    }

    // Метод для анализа уровня пульса
    fun analyzeBpm(bpm: Int): BpmLevel = when {
        bpm < 55 -> BpmLevel.LOW
        bpm < 70 -> BpmLevel.NORMAL
        bpm < 85 -> BpmLevel.ELEVATED
        else -> BpmLevel.HIGH
    }

    enum class BpmLevel {
        LOW, NORMAL, ELEVATED, HIGH
    }

    // Метод для обработки голосовой команды
    fun processVoiceCommand(command: String) {
         handleEvent(BreathingContract.Event.VoiceCommand(command))
    }
    
    // Метод для запуска прослушивания голосовой команды (передается в UI)
    fun startListeningVoice() {
        setEffect { BreathingContract.Effect.ShowError(UiError.Custom("Голосовой ввод пока не реализован")) }
    }
} 