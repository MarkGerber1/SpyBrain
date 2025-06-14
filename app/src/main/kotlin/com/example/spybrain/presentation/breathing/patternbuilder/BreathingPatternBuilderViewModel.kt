package com.example.spybrain.presentation.breathing.patternbuilder

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.spybrain.R
import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.domain.usecase.breathing.GetCustomBreathingPatternsUseCase
import com.example.spybrain.domain.usecase.breathing.AddCustomBreathingPatternUseCase
import com.example.spybrain.domain.usecase.breathing.DeleteCustomBreathingPatternUseCase
import com.example.spybrain.presentation.base.BaseViewModel
import com.example.spybrain.util.UiError
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BreathingPatternBuilderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getCustomPatternsUseCase: GetCustomBreathingPatternsUseCase,
    private val addCustomPatternUseCase: AddCustomBreathingPatternUseCase,
    private val deleteCustomPatternUseCase: DeleteCustomBreathingPatternUseCase
) : BaseViewModel<BreathingPatternBuilderContract.Event, BreathingPatternBuilderContract.State, BreathingPatternBuilderContract.Effect>() {

    init {
        viewModelScope.launch {
            getCustomPatternsUseCase().collect { patterns ->
                setState { copy(patterns = patterns) }
            }
        }
    }

    override fun createInitialState(): BreathingPatternBuilderContract.State =
        BreathingPatternBuilderContract.State()

    override fun handleEvent(event: BreathingPatternBuilderContract.Event) {
        when (event) {
            is BreathingPatternBuilderContract.Event.LoadPattern -> {
                val pattern = uiState.value.patterns.find { it.id == event.id }
                pattern?.let {
                    setState {
                        copy(
                            id = it.id,
                            name = it.name,
                            description = it.description.orEmpty(),
                            inhaleSeconds = it.inhaleSeconds.toString(),
                            holdAfterInhaleSeconds = it.holdAfterInhaleSeconds.toString(),
                            exhaleSeconds = it.exhaleSeconds.toString(),
                            holdAfterExhaleSeconds = it.holdAfterExhaleSeconds.toString(),
                            totalCycles = it.totalCycles.toString()
                        )
                    }
                }
            }
            is BreathingPatternBuilderContract.Event.EnterName -> {
                setState { copy(name = event.name) }
            }
            is BreathingPatternBuilderContract.Event.EnterDescription -> {
                setState { copy(description = event.description) }
            }
            is BreathingPatternBuilderContract.Event.EnterInhale -> {
                setState { copy(inhaleSeconds = event.seconds) }
            }
            is BreathingPatternBuilderContract.Event.EnterHoldInhale -> {
                setState { copy(holdAfterInhaleSeconds = event.seconds) }
            }
            is BreathingPatternBuilderContract.Event.EnterExhale -> {
                setState { copy(exhaleSeconds = event.seconds) }
            }
            is BreathingPatternBuilderContract.Event.EnterHoldExhale -> {
                setState { copy(holdAfterExhaleSeconds = event.seconds) }
            }
            is BreathingPatternBuilderContract.Event.EnterCycles -> {
                setState { copy(totalCycles = event.cycles) }
            }
            is BreathingPatternBuilderContract.Event.SavePattern -> {
                savePattern()
            }
            is BreathingPatternBuilderContract.Event.DeletePattern -> {
                deletePattern(event.pattern)
            }
        }
    }

    private fun savePattern() {
        viewModelScope.launch {
            try {
                val state = uiState.value
                // Валидация
                if (state.name.isBlank()) {
                    setEffect {
                        BreathingPatternBuilderContract.Effect.ShowError(
                            UiError.Custom(context.getString(R.string.pattern_builder_enter_name))
                        )
                    }
                    return@launch
                }
                val inhale = state.inhaleSeconds.toIntOrNull() ?: 0
                val exhale = state.exhaleSeconds.toIntOrNull() ?: 0
                val cycles = state.totalCycles.toIntOrNull() ?: 0
                if (inhale <= 0 || exhale <= 0 || cycles <= 0) {
                    setEffect {
                        BreathingPatternBuilderContract.Effect.ShowError(
                            UiError.Custom(context.getString(R.string.pattern_builder_invalid_values))
                        )
                    }
                    return@launch
                }
                val pattern = CustomBreathingPattern(
                    id = state.id ?: "custom_${System.currentTimeMillis()}",
                    name = state.name,
                    description = state.description,
                    inhaleSeconds = inhale,
                    holdAfterInhaleSeconds = state.holdAfterInhaleSeconds.toIntOrNull() ?: 0,
                    exhaleSeconds = exhale,
                    holdAfterExhaleSeconds = state.holdAfterExhaleSeconds.toIntOrNull() ?: 0,
                    totalCycles = cycles
                )
                addCustomPatternUseCase(pattern)
                Timber.d("Pattern saved: ${pattern.name}")
                setEffect { BreathingPatternBuilderContract.Effect.ShowSuccessMessage("Шаблон сохранен!") }
                setState {
                    copy(
                        id = null,
                        name = "",
                        description = "",
                        inhaleSeconds = "",
                        holdAfterInhaleSeconds = "",
                        exhaleSeconds = "",
                        holdAfterExhaleSeconds = "",
                        totalCycles = ""
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error saving pattern")
                setEffect { BreathingPatternBuilderContract.Effect.ShowError(UiError.UnknownError) }
            }
        }
    }

    private fun deletePattern(pattern: CustomBreathingPattern) {
        viewModelScope.launch {
            try {
                deleteCustomPatternUseCase(pattern)
                Timber.d("Pattern deleted: ${pattern.name}")
                setEffect { BreathingPatternBuilderContract.Effect.ShowSuccessMessage("Шаблон удален!") }
            } catch (e: Exception) {
                Timber.e(e, "Error deleting pattern")
                setEffect { BreathingPatternBuilderContract.Effect.ShowError(UiError.UnknownError) }
            }
        }
    }
} 