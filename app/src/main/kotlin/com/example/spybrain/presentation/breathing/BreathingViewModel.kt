package com.example.spybrain.presentation.breathing

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.spybrain.R
import com.example.spybrain.domain.model.BreathingPattern
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.SessionType
import com.example.spybrain.data.repository.BreathingPatternRepository
import com.example.spybrain.domain.repository.BreathingRepository
import com.example.spybrain.domain.usecase.breathing.TrackBreathingSessionUseCase
import com.example.spybrain.domain.usecase.stats.SaveSessionUseCase
import com.example.spybrain.presentation.base.BaseViewModel
import com.example.spybrain.domain.service.IVoiceAssistant
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject
import java.util.Locale
import com.example.spybrain.util.UiError
import com.example.spybrain.domain.error.ErrorHandler
import timber.log.Timber
import java.util.Date

@HiltViewModel
class BreathingViewModel @Inject constructor(
    private val breathingPatternRepository: BreathingPatternRepository,
    private val breathingRepository: BreathingRepository,
    private val trackBreathingSessionUseCase: TrackBreathingSessionUseCase,
    private val saveSessionUseCase: SaveSessionUseCase,
    private val voiceAssistant: IVoiceAssistant,
    @ApplicationContext private val context: Context
) : BaseViewModel<BreathingContract.Event, BreathingContract.State, BreathingContract.Effect>() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "Необработанная ошибка в корутине дыхания")
        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(exception))
        setEffect { BreathingContract.Effect.ShowError(uiError) }
    }

    private var breathingJob: Job? = null
    private var sessionStartTime: Long = 0

    init {
        loadPatterns()
    }

    override fun createInitialState(): BreathingContract.State = BreathingContract.State()

    override fun handleEvent(event: BreathingContract.Event) {
        when (event) {
            is BreathingContract.Event.LoadPatterns -> {
                loadPatterns()
            }
            is BreathingContract.Event.StartPattern -> {
                startPattern(event.pattern)
            }
            is BreathingContract.Event.StopPattern -> {
                stopPattern()
            }
            is BreathingContract.Event.VoiceCommand -> {
                handleVoiceCommand(event)
            }
        }
    }

    private fun handleVoiceCommand(event: BreathingContract.Event.VoiceCommand) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                val command = event.text.lowercase(Locale.getDefault())
                when {
                    command.contains(context.getString(R.string.voice_command_stop)) || command.contains("stop") -> stopPattern()
                    command.contains(context.getString(R.string.voice_command_start)) || command.contains("start") || command.contains(context.getString(R.string.voice_command_inhale)) -> {
                        uiState.value.patterns.firstOrNull()?.let { startPattern(it) }
                            ?: setEffect { BreathingContract.Effect.ShowError(UiError.Custom(context.getString(R.string.pattern_builder_no_patterns))) }
                    }
                    else -> setEffect { BreathingContract.Effect.Speak(context.getString(R.string.voice_command_not_recognized)) }
                }
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при обработке голосовой команды")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setEffect { BreathingContract.Effect.ShowError(uiError) }
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
                setEffect { BreathingContract.Effect.Speak(context.getString(R.string.breathing_start_inhale)) }
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
                    setEffect { BreathingContract.Effect.Speak(context.getString(R.string.breathing_phase_inhale)) }
                }
                
                if (!runPhase(BreathingContract.BreathingPhase.Inhale, pattern.inhaleSeconds, currentJob)) break
                
                // Hold After Inhale
                if (pattern.holdAfterInhaleSeconds > 0) {
                    if (voiceAssistant.isReady()) {
                        voiceAssistant.speakHold()
                    } else {
                        setEffect { BreathingContract.Effect.Speak(context.getString(R.string.breathing_phase_hold)) }
                    }
                    if (!runPhase(BreathingContract.BreathingPhase.HoldAfterInhale, pattern.holdAfterInhaleSeconds, currentJob)) break
                }
                
                // Exhale
                if (voiceAssistant.isReady()) {
                    voiceAssistant.speakExhale()
                } else {
                    setEffect { BreathingContract.Effect.Speak(context.getString(R.string.breathing_phase_exhale)) }
                }
                if (!runPhase(BreathingContract.BreathingPhase.Exhale, pattern.exhaleSeconds, currentJob)) break
                
                // Hold After Exhale
                if (pattern.holdAfterExhaleSeconds > 0) {
                    if (voiceAssistant.isReady()) {
                        voiceAssistant.speakRelax()
                    } else {
                        setEffect { BreathingContract.Effect.Speak(context.getString(R.string.breathing_phase_rest)) }
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
                    setEffect { BreathingContract.Effect.Speak(context.getString(R.string.breathing_complete_message)) }
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
                    id = System.currentTimeMillis().toString(),
                    type = SessionType.BREATHING,
                    startTime = Date(System.currentTimeMillis() - durationMillis),
                    endTime = Date(System.currentTimeMillis()),
                    durationSeconds = durationMillis / 1000,
                    relatedItemId = patternId
                )
                breathingRepository.trackBreathingSession(session)
                Timber.d("Session tracked: $patternId, duration: ${durationMillis}ms")
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при отслеживании сессии дыхания")
            }
        }
    }

    override fun onCleared() {
        try {
            breathingJob?.cancel()
            voiceAssistant.release()
            Timber.d("BreathingViewModel cleared")
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при очистке BreathingViewModel")
        }
        super.onCleared()
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
         setEvent(BreathingContract.Event.VoiceCommand(command))
    }
    
    // Метод для запуска прослушивания голосовой команды (передается в UI)
    fun startListeningVoice() {
        setEffect { BreathingContract.Effect.ShowError(UiError.Custom("Голосовой ввод пока не реализован")) }
    }
} 