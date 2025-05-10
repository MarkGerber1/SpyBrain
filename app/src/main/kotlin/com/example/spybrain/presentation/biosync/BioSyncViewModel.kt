package com.example.spybrain.presentation.biosync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.delay
import dagger.hilt.android.lifecycle.HiltViewModel

/**
 * ViewModel для BioSync: пока генерирует случайные BPM как заглушка.
 */
@HiltViewModel
class BioSyncViewModel @Inject constructor(): ViewModel() {
    private val _bpm = MutableStateFlow(0)
    val bpm: StateFlow<Int> = _bpm
    private var running = true

    init {
        viewModelScope.launch {
            while (running) {
                // эмулируем чтение BPM
                _bpm.value = Random.nextInt(50, 120)
                delay(1000L)
            }
        }
    }

    override fun onCleared() {
        running = false
        super.onCleared()
    }
} 