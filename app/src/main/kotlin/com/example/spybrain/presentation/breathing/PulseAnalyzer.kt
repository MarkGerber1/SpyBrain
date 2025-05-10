package com.example.spybrain.presentation.breathing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class PulseAnalyzer {
    private val _bpm = MutableStateFlow(75)
    val bpmFlow: StateFlow<Int> get() = _bpm

    private var running = false
    private val scope = CoroutineScope(Dispatchers.Default)

    fun start() {
        if (running) return
        running = true
        scope.launch {
            while (running) {
                _bpm.value = Random.nextInt(50, 120)
                delay(1000)
            }
        }
    }

    fun stop() {
        running = false
    }
} 