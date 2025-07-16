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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.spybrain.presentation.breathing.BreathingContract
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEffect

/**
 * ViewModel РґР»СЏ СѓРїСЂР°РІР»РµРЅРёСЏ Р»РѕРіРёРєРѕР№ РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїСЂР°РєС‚РёРє.
 * @param breathingPatternRepository Р РµРїРѕР·РёС‚РѕСЂРёР№ РїР°С‚С‚РµСЂРЅРѕРІ РґС‹С…Р°РЅРёСЏ.
 * @param breathingRepository Р РµРїРѕР·РёС‚РѕСЂРёР№ РґС‹С…Р°С‚РµР»СЊРЅС‹С… СЃРµСЃСЃРёР№.
 * @param trackBreathingSessionUseCase UseCase РґР»СЏ РѕС‚СЃР»РµР¶РёРІР°РЅРёСЏ СЃРµСЃСЃРёРё РґС‹С…Р°РЅРёСЏ.
 * @param saveSessionUseCase UseCase РґР»СЏ СЃРѕС…СЂР°РЅРµРЅРёСЏ СЃРµСЃСЃРёРё.
 * @param voiceAssistant Р“РѕР»РѕСЃРѕРІРѕР№ Р°СЃСЃРёСЃС‚РµРЅС‚.
 * @param context РљРѕРЅС‚РµРєСЃС‚ РїСЂРёР»РѕР¶РµРЅРёСЏ.
 */
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
        Timber.e(exception, "РќРµРѕР±СЂР°Р±РѕС‚Р°РЅРЅР°СЏ РѕС€РёР±РєР° РІ РєРѕСЂСѓС‚РёРЅРµ РґС‹С…Р°РЅРёСЏ")
        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(exception))
        setEffect { BreathingContract.Effect.ShowError(uiError) }
    }

    private var breathingJob: Job? = null
    private var sessionStartTime: Long = 0

    init {
        loadPatterns()
    }

    /**
     * РЎРѕР·РґР°С‘С‚ РЅР°С‡Р°Р»СЊРЅРѕРµ СЃРѕСЃС‚РѕСЏРЅРёРµ СЌРєСЂР°РЅР° РґС‹С…Р°РЅРёСЏ.
     * @return РќР°С‡Р°Р»СЊРЅРѕРµ СЃРѕСЃС‚РѕСЏРЅРёРµ.
     */
    override fun createInitialState(): BreathingContract.State = BreathingContract.State()

    /**
     * РћР±СЂР°Р±Р°С‚С‹РІР°РµС‚ СЃРѕР±С‹С‚РёРµ UI.
     * @param event РЎРѕР±С‹С‚РёРµ UI.
     */
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
                    command.contains(context.getString(R.string.voice_command_start)) ||
                        command.contains("start") ||
                        command.contains(context.getString(R.string.voice_command_inhale)) -> {
                        uiState.value.patterns.firstOrNull()?.let { startPattern(it) }
                            ?: setEffect { BreathingContract.Effect.ShowError(UiError.Custom(context.getString(R.string.pattern_builder_no_patterns))) }
                    }
                    else -> setEffect { BreathingContract.Effect.Speak(context.getString(R.string.voice_command_not_recognized)) }
                }
            } catch (e: Exception) {
                Timber.e(e, "РћС€РёР±РєР° РїСЂРё РѕР±СЂР°Р±РѕС‚РєРµ РіРѕР»РѕСЃРѕРІРѕР№ РєРѕРјР°РЅРґС‹")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setEffect { BreathingContract.Effect.ShowError(uiError) }
            }
        }
    }

    private fun loadPatterns() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                setState { copy(isLoading = true) }

                val patterns = breathingPatternRepository.getAllPatterns()
                setState { copy(isLoading = false, patterns = patterns) }

                Timber.d("Loaded ${patterns.size} breathing patterns")

            } catch (e: Exception) {
                Timber.e(e, "РљСЂРёС‚РёС‡РµСЃРєР°СЏ РѕС€РёР±РєР° РїСЂРё Р·Р°РіСЂСѓР·РєРµ РїР°С‚С‚РµСЂРЅРѕРІ")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setState { copy(isLoading = false) }
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

            // Р“РѕР»РѕСЃРѕРІРѕРµ СЃРѕРїСЂРѕРІРѕР¶РґРµРЅРёРµ СЃС‚Р°СЂС‚Р°
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
            Timber.e(e, "РћС€РёР±РєР° РїСЂРё Р·Р°РїСѓСЃРєРµ РїР°С‚С‚РµСЂРЅР° РґС‹С…Р°РЅРёСЏ")
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

                // РњРѕС‚РёРІР°С†РёСЏ РєР°Р¶РґС‹Рµ 3 С†РёРєР»Р°
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
            Timber.e(e, "РћС€РёР±РєР° РІ С†РёРєР»Рµ РґС‹С…Р°РЅРёСЏ")
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
            Timber.e(e, "РћС€РёР±РєР° РІ С„Р°Р·Рµ РґС‹С…Р°РЅРёСЏ: $phase")
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
            Timber.e(e, "РћС€РёР±РєР° РїСЂРё РѕСЃС‚Р°РЅРѕРІРєРµ РїР°С‚С‚РµСЂРЅР°")
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
            Timber.e(e, "РћС€РёР±РєР° РїСЂРё РІРЅСѓС‚СЂРµРЅРЅРµР№ РѕСЃС‚Р°РЅРѕРІРєРµ РїР°С‚С‚РµСЂРЅР°")
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
                Timber.e(e, "РћС€РёР±РєР° РїСЂРё РѕС‚СЃР»РµР¶РёРІР°РЅРёРё СЃРµСЃСЃРёРё РґС‹С…Р°РЅРёСЏ")
            }
        }
    }

    override fun onCleared() {
        try {
            breathingJob?.cancel()
            voiceAssistant.release()
            Timber.d("BreathingViewModel cleared")
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РїСЂРё РѕС‡РёСЃС‚РєРµ BreathingViewModel")
        }
        super.onCleared()
    }

    /**
     * РЈСЂРѕРІРµРЅСЊ РїСѓР»СЊСЃР° РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    enum class BpmLevel {
        LOW, NORMAL, ELEVATED, HIGH
    }

    /**
     * РђРЅР°Р»РёР·РёСЂСѓРµС‚ СѓСЂРѕРІРµРЅСЊ РїСѓР»СЊСЃР°.
     * @param bpm Р—РЅР°С‡РµРЅРёРµ РїСѓР»СЊСЃР°.
     * @return РЈСЂРѕРІРµРЅСЊ РїСѓР»СЊСЃР°.
     */
    fun analyzeBpm(bpm: Int): BpmLevel = when {
        bpm < 55 -> BpmLevel.LOW
        bpm < 70 -> BpmLevel.NORMAL
        bpm < 85 -> BpmLevel.ELEVATED
        else -> BpmLevel.HIGH
    }

    /**
     * РћР±СЂР°Р±Р°С‚С‹РІР°РµС‚ РіРѕР»РѕСЃРѕРІСѓСЋ РєРѕРјР°РЅРґСѓ.
     * @param command РљРѕРјР°РЅРґР° РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    fun processVoiceCommand(command: String) {
        setEvent(BreathingContract.Event.VoiceCommand(command))
    }

    /**
     * Р—Р°РїСѓСЃРєР°РµС‚ РїСЂРѕСЃР»СѓС€РёРІР°РЅРёРµ РіРѕР»РѕСЃРѕРІРѕР№ РєРѕРјР°РЅРґС‹.
     */
    fun startListeningVoice() {
        setEffect { BreathingContract.Effect.ShowError(UiError.Custom("Р“РѕР»РѕСЃРѕРІРѕР№ РІРІРѕРґ РїРѕРєР° РЅРµ СЂРµР°Р»РёР·РѕРІР°РЅ")) }
    }
}
