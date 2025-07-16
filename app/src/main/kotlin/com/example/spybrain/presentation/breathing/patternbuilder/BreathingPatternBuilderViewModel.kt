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
import com.example.spybrain.domain.error.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEffect

/**
 * ViewModel для конструктора дыхательных паттернов.
 */
@HiltViewModel
class BreathingPatternBuilderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getCustomPatternsUseCase: GetCustomBreathingPatternsUseCase,
    private val addCustomPatternUseCase: AddCustomBreathingPatternUseCase,
    private val deleteCustomPatternUseCase: DeleteCustomBreathingPatternUseCase
) : BaseViewModel<BreathingPatternBuilderContract.Event, BreathingPatternBuilderContract.State, BreathingPatternBuilderContract.Effect>() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "Необработанная ошибка в корутине создания паттернов")
        val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(exception))
        setEffect { BreathingPatternBuilderContract.Effect.ShowError(uiError) }
    }

    init {
        loadPatterns()
    }

    private fun loadPatterns() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                setState { copy(isLoading = true, error = null) }
                val patterns = getCustomPatternsUseCase()
                setState { copy(isLoading = false, patterns = patterns) }
                Timber.d("Загружено ${patterns.size} пользовательских паттернов")
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при загрузке паттернов")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setState { copy(isLoading = false, error = uiError) }
                setEffect { BreathingPatternBuilderContract.Effect.ShowError(uiError) }
            }
        }
    }

    /**
     * Создает начальное состояние конструктора паттернов.
     * @return Начальное состояние.
     */
    override fun createInitialState(): BreathingPatternBuilderContract.State =
        BreathingPatternBuilderContract.State()

    /**
     * Обрабатывает событие UI.
     * @param event Событие UI.
     */
    override fun handleEvent(event: BreathingPatternBuilderContract.Event) {
        when (event) {
            is BreathingPatternBuilderContract.Event.LoadPattern -> {
                loadPattern(event.id)
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

    private fun loadPattern(id: Long) {
        try {
            val pattern = uiState.value.patterns.find { it.id == id }
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
                Timber.d("Паттерн загружен: ${it.name}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при загрузке паттерна")
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
            setEffect { BreathingPatternBuilderContract.Effect.ShowError(uiError) }
        }
    }

    private fun savePattern() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                setState { copy(isLoading = true, error = null) }
                val state = uiState.value
                if (state.name.isBlank()) {
                    setEffect {
                        BreathingPatternBuilderContract.Effect.ShowError(
                            UiError.Custom(context.getString(R.string.pattern_builder_enter_name))
                        )
                    }
                    setState { copy(isLoading = false) }
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
                    setState { copy(isLoading = false) }
                    return@launch
                }
                if (inhale > 60 || exhale > 60 || cycles > 100) {
                    setEffect {
                        BreathingPatternBuilderContract.Effect.ShowError(
                            UiError.Custom("Значения слишком большие. Проверьте параметры.")
                        )
                    }
                    setState { copy(isLoading = false) }
                    return@launch
                }
                val pattern = CustomBreathingPattern(
                    id = state.id ?: System.currentTimeMillis(),
                    name = state.name.trim(),
                    description = state.description.trim(),
                    inhaleSeconds = inhale,
                    holdAfterInhaleSeconds = state.holdAfterInhaleSeconds.toIntOrNull() ?: 0,
                    exhaleSeconds = exhale,
                    holdAfterExhaleSeconds = state.holdAfterExhaleSeconds.toIntOrNull() ?: 0,
                    totalCycles = cycles
                )
                addCustomPatternUseCase(pattern)
                Timber.d("Паттерн сохранен: ${pattern.name}")
                setEffect { BreathingPatternBuilderContract.Effect.ShowSuccessMessage("Шаблон сохранен!") }
                setState {
                    copy(
                        isLoading = false,
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
                Timber.e(e, "Ошибка при сохранении паттерна")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setState { copy(isLoading = false, error = uiError) }
                setEffect { BreathingPatternBuilderContract.Effect.ShowError(uiError) }
            }
        }
    }

    private fun deletePattern(pattern: CustomBreathingPattern) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                setState { copy(isLoading = true, error = null) }
                deleteCustomPatternUseCase(pattern.id)
                Timber.d("Паттерн удален: ${pattern.name}")
                setEffect { BreathingPatternBuilderContract.Effect.ShowSuccessMessage("Шаблон удален!") }
                setState { copy(isLoading = false) }
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при удалении паттерна")
                val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(e))
                setState { copy(isLoading = false, error = uiError) }
                setEffect { BreathingPatternBuilderContract.Effect.ShowError(uiError) }
            }
        }
    }
}
