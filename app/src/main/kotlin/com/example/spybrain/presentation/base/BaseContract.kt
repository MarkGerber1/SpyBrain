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

/**
 * Базовый интерфейс для состояния UI.
 */
public interface UiState

/**
 * Базовый интерфейс для событий UI.
 */
public interface UiEvent

/**
 * Базовый интерфейс для эффектов UI.
 */
public interface UiEffect

/**
 * Базовая ViewModel для MVI-архитектуры.
 * @param Event Тип событий.
 * @param State Тип состояния.
 * @param Effect Тип эффектов.
 */
abstract class BaseViewModel<Event : UiEvent, State : UiState, Effect : UiEffect> : ViewModel() {
    /** Начальное состояние. */
    private val initialState: State by lazy { createInitialState() }
    
    /** Создает начальное состояние. */
    abstract fun createInitialState(): State
    
    /** StateFlow текущего состояния UI. */
    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    
    /** Публичный StateFlow состояния UI. */
    val uiState: StateFlow<State> = _uiState.asStateFlow()
    
    /** SharedFlow событий. */
    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    
    /** Channel для эффектов. */
    private val _effect: Channel<Effect> = Channel()
    
    /** Flow эффектов. */
    val effect: Flow<Effect> = _effect.receiveAsFlow()
    
    init {
        subscribeEvents()
    }
    
    /** Подписка на события. */
    private fun subscribeEvents() {
        viewModelScope.launch {
            _event.collect {
                handleEvent(it)
            }
        }
    }
    
    /** Обработка события.
     * @param event Событие UI.
     */
    abstract fun handleEvent(event: Event)
    
    /** Установить событие.
     * @param event Событие UI.
     */
    fun setEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }
    
    /** Установить новое состояние.
     * @param reduce Лямбда для изменения состояния.
     */
    protected fun setState(reduce: State.() -> State) {
        val newState = uiState.value.reduce()
        _uiState.value = newState
    }
    
    /** Установить эффект.
     * @param builder Лямбда для создания эффекта.
     */
    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }
}
