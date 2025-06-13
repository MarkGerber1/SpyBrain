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

/**
 * ViewModel для BioSync: пока генерирует случайные BPM как заглушка.
 */
@HiltViewModel
class BioSyncViewModel @Inject constructor(): ViewModel() {
    private val _bpm = MutableStateFlow(0)
    val bpm: StateFlow<Int> = _bpm
    
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "Ошибка в BioSync корутине")
    }
    
    private var bpmJob: Job? = null

    init {
        startBpmMonitoring()
    }
    
    private fun startBpmMonitoring() {
        bpmJob = viewModelScope.launch(coroutineExceptionHandler) {
            try {
                while (isActive) {
                    // эмулируем чтение BPM
                    _bpm.value = Random.nextInt(50, 120)
                    delay(1000L)
                }
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при мониторинге BPM")
            }
        }
    }

    override fun onCleared() {
        try {
            bpmJob?.cancel()
            bpmJob = null
            super.onCleared()
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при очистке BioSyncViewModel")
        }
    }
} 