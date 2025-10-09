package com.example.spybrain.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Интерфейс маркер для состояния UI.
 * Все состояния должны реализовывать этот интерфейс.
 */
public interface UiState

/**
 * Интерфейс маркер для событий UI.
 * Все события должны реализовывать этот интерфейс.
 */
public interface UiEvent

/**
 * Интерфейс маркер для эффектов UI.
 * Все эффекты должны реализовывать этот интерфейс.
 */
public interface UiEffect

/**
 * Базовая ViewModel для MVI-архитектуры.
 * Предоставляет общую логику обработки событий, состояний и эффектов.
 *
 * @param Event Тип событий UI.
 * @param State Тип состояния UI.
 * @param Effect Тип эффектов UI.
 */
abstract class BaseViewModel<Event : UiEvent, State : UiState, Effect : UiEffect>(
    initialState: State
) : ViewModel() {

    /** Текущее состояние UI. */
    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    /** Публичный StateFlow состояния UI. */
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    /** SharedFlow для событий. */
    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()

    /** Channel для эффектов. */
    private val _effect: Channel<Effect> = Channel(Channel.BUFFERED)
    /** Публичный Flow эффектов. */
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    /** Подписка на события в корутине viewModelScope. */
    private fun subscribeToEvents() {
        viewModelScope.launch {
            _event.collect { event ->
                try {
                    handleEvent(event)
                } catch (e: Exception) {
                    Timber.e(e, "Ошибка обработки события: $event")
                    handleError(e)
                }
            }
        }
    }

    /**
     * Обработка события UI.
     * Должна быть реализована в наследниках.
     *
     * @param event Событие для обработки.
     */
    abstract fun handleEvent(event: Event)

    /**
     * Обработка ошибок в ViewModel.
     * Может быть переопределена для кастомной логики.
     *
     * @param throwable Исключение для обработки.
     */
    protected open fun handleError(throwable: Throwable) {
        Timber.e(throwable, "Необработанная ошибка в ViewModel")
    }

    /**
     * Отправка события в ViewModel.
     *
     * @param event Событие для обработки.
     */
    fun setEvent(event: Event) {
        viewModelScope.launch {
            try {
                _event.emit(event)
            } catch (e: Exception) {
                Timber.e(e, "Ошибка отправки события: $event")
            }
        }
    }

    /**
     * Обновление состояния UI.
     * Создает новое состояние на основе текущего.
     *
     * @param reducer Функция для преобразования состояния.
     */
    protected fun setState(reducer: State.() -> State) {
        try {
            val newState = uiState.value.reducer()
            _uiState.value = newState
        } catch (e: Exception) {
            Timber.e(e, "Ошибка обновления состояния")
        }
    }

    /**
     * Отправка эффекта UI.
     *
     * @param effect Эффект для отправки.
     */
    protected fun setEffect(effect: Effect) {
        viewModelScope.launch {
            try {
                _effect.send(effect)
            } catch (e: Exception) {
                Timber.e(e, "Ошибка отправки эффекта: $effect")
            }
        }
    }

    /**
     * Отправка эффекта UI с помощью builder-функции.
     *
     * @param builder Функция для создания эффекта.
     */
    protected fun setEffect(builder: () -> Effect) {
        try {
            val effect = builder()
            setEffect(effect)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка создания эффекта")
        }
    }

    /**
     * Безопасное выполнение асинхронной операции.
     * Автоматически обрабатывает ошибки и обновляет состояние загрузки.
     *
     * @param showLoading Показывать ли индикатор загрузки.
     * @param block Асинхронная операция для выполнения.
     */
    protected fun launchAsync(
        showLoading: Boolean = false,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (showLoading) {
                    // Можно добавить логику показа загрузки
                }
                block()
            } catch (e: Exception) {
                Timber.e(e, "Ошибка асинхронной операции")
                handleError(e)
            } finally {
                if (showLoading) {
                    // Можно добавить логику скрытия загрузки
                }
            }
        }
    }
}