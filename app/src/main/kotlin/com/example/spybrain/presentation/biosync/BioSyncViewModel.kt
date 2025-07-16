package com.example.spybrain.presentation.biosync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.delay
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import androidx.lifecycle.SavedStateHandle
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.presentation.biosync.BioSyncContract

/**
 * @constructor Р’РЅРµРґСЂРµРЅРёРµ Р·Р°РІРёСЃРёРјРѕСЃС‚РµР№ С‡РµСЂРµР· Hilt.
 */
@HiltViewModel
class BioSyncViewModel @Inject constructor(): ViewModel() {
    private val _bpm = MutableStateFlow(0)
    /** StateFlow С‚РµРєСѓС‰РµРіРѕ BPM. */
    val bpm: StateFlow<Int> = _bpm

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "РћС€РёР±РєР° РІ BioSync РєРѕСЂСѓС‚РёРЅРµ")
    }

    private var bpmJob: Job? = null

    init {
        startBpmMonitoring()
    }

    private fun startBpmMonitoring() {
        bpmJob = viewModelScope.launch(coroutineExceptionHandler) {
            try {
                while (isActive) {
                    // СЌРјСѓР»РёСЂСѓРµРј С‡С‚РµРЅРёРµ BPM
                    _bpm.value = Random.nextInt(50, 120)
                    delay(1000L)
                }
            } catch (e: Exception) {
                Timber.e(e, "РћС€РёР±РєР° РїСЂРё РјРѕРЅРёС‚РѕСЂРёРЅРіРµ BPM")
            }
        }
    }

    /**
     * Р’С‹Р·С‹РІР°РµС‚СЃСЏ РїСЂРё СѓРЅРёС‡С‚РѕР¶РµРЅРёРё ViewModel.
     * РћСЃС‚Р°РЅР°РІР»РёРІР°РµС‚ РјРѕРЅРёС‚РѕСЂРёРЅРі BPM.
     */
    override fun onCleared() {
        try {
            bpmJob?.cancel()
            bpmJob = null
            super.onCleared()
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РїСЂРё РѕС‡РёСЃС‚РєРµ BioSyncViewModel")
        }
    }
}
