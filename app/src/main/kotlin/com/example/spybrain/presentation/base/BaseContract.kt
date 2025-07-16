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
 */
public interface UiState
/**
 */
public interface UiEvent
/**
 */
public interface UiEffect

/**
 * Р‘Р°Р·РѕРІР°СЏ ViewModel РґР»СЏ MVI-Р°СЂС…РёС‚РµРєС‚СѓСЂС‹.
 * @param Event РўРёРї СЃРѕР±С‹С‚РёР№.
 * @param State РўРёРї СЃРѕСЃС‚РѕСЏРЅРёСЏ.
 * @param Effect РўРёРї СЌС„С„РµРєС‚РѕРІ.
 */
abstract class BaseViewModel<Event : UiEvent, State : UiState, Effect : UiEffect> : ViewModel() {
    /** РќР°С‡Р°Р»СЊРЅРѕРµ СЃРѕСЃС‚РѕСЏРЅРёРµ. */
    private val initialState: State by lazy { createInitialState() }
    /** РЎРѕР·РґР°С‘С‚ РЅР°С‡Р°Р»СЊРЅРѕРµ СЃРѕСЃС‚РѕСЏРЅРёРµ. */
    abstract fun createInitialState(): State
    /** StateFlow С‚РµРєСѓС‰РµРіРѕ СЃРѕСЃС‚РѕСЏРЅРёСЏ UI. */
    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    /** РџСѓР±Р»РёС‡РЅС‹Р№ StateFlow СЃРѕСЃС‚РѕСЏРЅРёСЏ UI. */
    val uiState: StateFlow<State> = _uiState.asStateFlow()
    /** SharedFlow СЃРѕР±С‹С‚РёР№. */
    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    /** Channel РґР»СЏ СЌС„С„РµРєС‚РѕРІ. */
    private val _effect: Channel<Effect> = Channel()
    /** Flow СЌС„С„РµРєС‚РѕРІ. */
    val effect: Flow<Effect> = _effect.receiveAsFlow()
    init {
        subscribeEvents()
    }
    /** РџРѕРґРїРёСЃРєР° РЅР° СЃРѕР±С‹С‚РёСЏ. */
    private fun subscribeEvents() {
        viewModelScope.launch {
            _event.collect {
                handleEvent(it)
            }
        }
    }
    /** РћР±СЂР°Р±РѕС‚РєР° СЃРѕР±С‹С‚РёСЏ.
     * @param event РЎРѕР±С‹С‚РёРµ UI.
     */
    abstract fun handleEvent(event: Event)
    /** РЈСЃС‚Р°РЅРѕРІРёС‚СЊ СЃРѕР±С‹С‚РёРµ.
     * @param event РЎРѕР±С‹С‚РёРµ UI.
     */
    fun setEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }
    /** РЈСЃС‚Р°РЅРѕРІРёС‚СЊ РЅРѕРІРѕРµ СЃРѕСЃС‚РѕСЏРЅРёРµ.
     * @param reduce Р›СЏРјР±РґР° РґР»СЏ РёР·РјРµРЅРµРЅРёСЏ СЃРѕСЃС‚РѕСЏРЅРёСЏ.
     */
    protected fun setState(reduce: State.() -> State) {
        val newState = uiState.value.reduce()
        _uiState.value = newState
    }
    /** РЈСЃС‚Р°РЅРѕРІРёС‚СЊ СЌС„С„РµРєС‚.
     * @param builder Р›СЏРјР±РґР° РґР»СЏ СЃРѕР·РґР°РЅРёСЏ СЌС„С„РµРєС‚Р°.
     */
    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }
}
