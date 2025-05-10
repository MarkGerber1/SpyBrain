package com.example.spybrain.presentation.breathing.patternbuilder

import androidx.lifecycle.viewModelScope
import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.domain.usecase.breathing.GetCustomBreathingPatternsUseCase
import com.example.spybrain.domain.usecase.breathing.AddCustomBreathingPatternUseCase
import com.example.spybrain.domain.usecase.breathing.DeleteCustomBreathingPatternUseCase
import com.example.spybrain.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import com.example.spybrain.domain.error.ErrorHandler // FIXME билд-фикс 09.05.2025
import com.example.spybrain.util.UiError // FIXME билд-фикс 09.05.2025

@HiltViewModel
class BreathingPatternBuilderViewModel @Inject constructor(
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
                viewModelScope.launch {
                    // load one pattern by ID and populate state
                    val pattern = getCustomPatternsUseCase()
                        .first()
                        .firstOrNull { it.id == event.id }
                    pattern?.let {
                        setState {
                            copy(
                                name = it.name,
                                description = it.description.orEmpty(),
                                inhaleSeconds = it.inhaleSeconds.toString(),
                                holdAfterInhaleSeconds = it.holdAfterInhaleSeconds.toString(),
                                exhaleSeconds = it.exhaleSeconds.toString(),
                                holdAfterExhaleSeconds = it.holdAfterExhaleSeconds.toString(),
                                totalCycles = it.totalCycles.toString(),
                                id = it.id
                            )
                        }
                    }
                }
                return
            }
            is BreathingPatternBuilderContract.Event.EnterName ->
                setState { copy(name = event.name) }
            is BreathingPatternBuilderContract.Event.EnterDescription ->
                setState { copy(description = event.description) }
            is BreathingPatternBuilderContract.Event.EnterInhale ->
                setState { copy(inhaleSeconds = event.seconds) }
            is BreathingPatternBuilderContract.Event.EnterHoldInhale ->
                setState { copy(holdAfterInhaleSeconds = event.seconds) }
            is BreathingPatternBuilderContract.Event.EnterExhale ->
                setState { copy(exhaleSeconds = event.seconds) }
            is BreathingPatternBuilderContract.Event.EnterHoldExhale ->
                setState { copy(holdAfterExhaleSeconds = event.seconds) }
            is BreathingPatternBuilderContract.Event.EnterCycles ->
                setState { copy(totalCycles = event.cycles) }
            is BreathingPatternBuilderContract.Event.SavePattern -> {
                val current = uiState.value
                if (current.name.isBlank()) {
                    setEffect { BreathingPatternBuilderContract.Effect.ShowError(UiError.Custom("Введите название шаблона")) }
                    return
                }
                val inhale = current.inhaleSeconds.toIntOrNull()
                val holdInhale = current.holdAfterInhaleSeconds.toIntOrNull()
                val exhale = current.exhaleSeconds.toIntOrNull()
                val holdExhale = current.holdAfterExhaleSeconds.toIntOrNull()
                val cycles = current.totalCycles.toIntOrNull()
                if (listOf(inhale, holdInhale, exhale, holdExhale, cycles).any { it == null || it <= 0 }) {
                    setEffect { BreathingPatternBuilderContract.Effect.ShowError(UiError.Custom("Неверные значения фаз или циклов")) }
                    return
                }
                val pattern = CustomBreathingPattern(
                    name = current.name,
                    description = current.description.takeIf { it.isNotBlank() },
                    inhaleSeconds = inhale!!,
                    holdAfterInhaleSeconds = holdInhale!!,
                    exhaleSeconds = exhale!!,
                    holdAfterExhaleSeconds = holdExhale!!,
                    totalCycles = cycles!!
                )
                viewModelScope.launch {
                    addCustomPatternUseCase(pattern)
                }
                // очищаем поля ввода
                setState {
                    copy(
                        name = "",
                        description = "",
                        inhaleSeconds = "",
                        holdAfterInhaleSeconds = "",
                        exhaleSeconds = "",
                        holdAfterExhaleSeconds = "",
                        totalCycles = ""
                    )
                }
            }
            is BreathingPatternBuilderContract.Event.DeletePattern -> {
                viewModelScope.launch {
                    deleteCustomPatternUseCase(event.pattern)
                }
            }
        }
    }
} 