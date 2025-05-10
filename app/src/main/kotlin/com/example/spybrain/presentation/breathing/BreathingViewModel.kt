package com.example.spybrain.presentation.breathing

import androidx.lifecycle.viewModelScope
import com.example.spybrain.domain.model.BreathingPattern
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.SessionType
import com.example.spybrain.domain.usecase.breathing.GetBreathingPatternsUseCase
import com.example.spybrain.domain.usecase.breathing.TrackBreathingSessionUseCase
import com.example.spybrain.domain.usecase.stats.SaveSessionUseCase
import com.example.spybrain.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import com.example.spybrain.presentation.breathing.BreathingContract.Effect.Speak
import java.util.Locale
import com.example.spybrain.domain.service.IAiMentor
import com.example.spybrain.util.UiError
import com.example.spybrain.domain.service.IHealthAdvisor
import com.example.spybrain.domain.service.IVoiceAssistant
import com.example.spybrain.domain.error.ErrorHandler // FIXME билд-фикс 09.05.2025

@HiltViewModel
class BreathingViewModel @Inject constructor(
    private val getBreathingPatternsUseCase: GetBreathingPatternsUseCase,
    private val trackBreathingSessionUseCase: TrackBreathingSessionUseCase,
    private val saveSessionUseCase: SaveSessionUseCase,
    private val aiMentor: IAiMentor,
    private val healthAdvisor: IHealthAdvisor,
    private val voiceAssistant: IVoiceAssistant
) : BaseViewModel<BreathingContract.Event, BreathingContract.State, BreathingContract.Effect>() { // TODO: Добавить все необходимые юнит-тесты для ViewModel

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
                val command = event.text.lowercase(Locale.getDefault())
                when {
                    command.contains("стоп") || command.contains("stop") -> stopPattern()
                    command.contains("начать") || command.contains("start") || command.contains("вдох") -> {
                        uiState.value.patterns.firstOrNull()?.let { startPattern(it) }
                            ?: setEffect { BreathingContract.Effect.ShowError(UiError.Custom("Нет доступных шаблонов")) }
                    }
                    else -> setEffect { BreathingContract.Effect.Speak("Команда не распознана") }
                }
            }
        }
    }

    private fun loadPatterns() {
        viewModelScope.launch {
            getBreathingPatternsUseCase()
                .onStart { setState { copy(isLoading = true, error = null) } }
                .catch { error ->
                    val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(error))
                    setState { copy(isLoading = false, error = uiError) }
                    setEffect { BreathingContract.Effect.ShowError(uiError) }
                }
                .collect { patterns ->
                    setState { copy(isLoading = false, patterns = patterns) }
                }
        }
    }

    private fun startPattern(pattern: BreathingPattern) {
        stopPattern() // Stop any existing pattern
        sessionStartTime = System.currentTimeMillis()
        setState {
            copy(
                currentPattern = pattern,
                remainingCycles = pattern.totalCycles,
                currentPhase = BreathingContract.BreathingPhase.Idle // Start with idle before first inhale
            )
        }
        // Голосовое сопровождение старта
        setEffect { Speak("Начинаем. Вдох...") }
        breathingJob = viewModelScope.launch {
            runBreathingCycle(pattern)
        }
    }

    private suspend fun runBreathingCycle(pattern: BreathingPattern) {
        val currentJob = breathingJob ?: return // Ensure job exists
        while (currentJob.isActive && uiState.value.remainingCycles > 0) {
            // Inhale
            setEffect { Speak("Вдох") }
            aiMentor.giveBreathingAdvice(BreathingContract.BreathingPhase.Inhale)
            if (!runPhase(BreathingContract.BreathingPhase.Inhale, pattern.inhaleSeconds, currentJob)) break
            // Hold After Inhale
            if (pattern.holdAfterInhaleSeconds > 0) {
                setEffect { Speak("Задержка") }
                aiMentor.giveBreathingAdvice(BreathingContract.BreathingPhase.HoldAfterInhale)
                if (!runPhase(BreathingContract.BreathingPhase.HoldAfterInhale, pattern.holdAfterInhaleSeconds, currentJob)) break
            }
            // Exhale
            setEffect { Speak("Выдох") }
            aiMentor.giveBreathingAdvice(BreathingContract.BreathingPhase.Exhale)
            if (!runPhase(BreathingContract.BreathingPhase.Exhale, pattern.exhaleSeconds, currentJob)) break
            // Hold After Exhale
            if (pattern.holdAfterExhaleSeconds > 0) {
                setEffect { Speak("Отдых") }
                aiMentor.giveBreathingAdvice(BreathingContract.BreathingPhase.HoldAfterExhale)
                if (!runPhase(BreathingContract.BreathingPhase.HoldAfterExhale, pattern.holdAfterExhaleSeconds, currentJob)) break
            }

            setState { copy(remainingCycles = remainingCycles - 1) }
        }
        // Cycle finished or stopped
        if (currentJob.isActive) {
            // Отслеживание и голос завершения
            trackSessionEnd(pattern.id, System.currentTimeMillis() - sessionStartTime)
            setEffect { Speak("Отлично! Вы завершили дыхательную практику.") }
            stopPatternInternal()
        }
    }

    private suspend fun runPhase(phase: BreathingContract.BreathingPhase, durationSeconds: Int, job: Job): Boolean {
        if (durationSeconds <= 0) return true
        setState { copy(currentPhase = phase, cycleProgress = 0f) }
        setEffect { BreathingContract.Effect.Vibrate } // Vibrate at phase start
        val stepMillis = 100L // Update progress every 100ms
        val totalSteps = (durationSeconds * 1000 / stepMillis).toInt()

        for (step in 1..totalSteps) {
            if (!job.isActive) return false // Check specific job
            delay(stepMillis)
            setState { copy(cycleProgress = step.toFloat() / totalSteps) }
        }
         setState { copy(cycleProgress = 1f) } // Ensure progress reaches 1
        return job.isActive // Return true if job is still active after delay
    }


    private fun stopPattern() {
        val pattern = uiState.value.currentPattern
        val startTime = sessionStartTime
        stopPatternInternal()
        pattern?.let {
            val durationMillis = System.currentTimeMillis() - startTime
            if (durationMillis > 1000) { // Only track if session was longer than 1 second
                 trackSessionEnd(it.id, durationMillis)
            }
        }
    }

    private fun stopPatternInternal() {
        breathingJob?.cancel()
        breathingJob = null
        setState { copy(currentPattern = null, currentPhase = BreathingContract.BreathingPhase.Idle, remainingCycles = 0, cycleProgress = 0f) }
        sessionStartTime = 0
    }

    private fun trackSessionEnd(patternId: String, durationMillis: Long) {
        viewModelScope.launch {
            try {
                val session = Session(
                    id = "session_${System.currentTimeMillis()}",
                    type = SessionType.BREATHING,
                    startTime = Date(sessionStartTime),
                    endTime = Date(),
                    durationSeconds = durationMillis / 1000,
                    relatedItemId = patternId
                )
                // Save session in stats repository
                saveSessionUseCase(session)
                // Track breathing session in repository
                trackBreathingSessionUseCase(session)
            } catch (e: Exception) {
                 val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setEffect { BreathingContract.Effect.ShowError(uiError) }
            }
        }
    }

    override fun onCleared() {
        stopPatternInternal()
        super.onCleared()
    }

    // Метод для анализа уровня пульса
    fun analyzeBpm(bpm: Int): BpmLevel = when {
        bpm < 55 -> BpmLevel.LOW
        bpm <= 90 -> BpmLevel.NORMAL
        bpm <= 110 -> BpmLevel.HIGH
        else -> BpmLevel.CRITICAL
    }
    
    // Уровни пульса
    enum class BpmLevel { LOW, NORMAL, HIGH, CRITICAL }

    // Метод для получения динамических советов от HealthAdvisor
    fun getDynamicAdvices(phase: BreathingContract.BreathingPhase, bpm: Int): String {
        return healthAdvisor.getAdvices(phase, bpm).joinToString(" • ")
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