package com.example.spybrain.presentation.breathing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * РђРЅР°Р»РёР·Р°С‚РѕСЂ РїСѓР»СЊСЃР° РґР»СЏ СЃРёРјСѓР»СЏС†РёРё BPM.
 */
class PulseAnalyzer {
    private val _bpm = MutableStateFlow(75)

    /**
     * РџРѕС‚РѕРє С‚РµРєСѓС‰РµРіРѕ Р·РЅР°С‡РµРЅРёСЏ РїСѓР»СЊСЃР° (BPM).
     */
    val bpmFlow: StateFlow<Int> get() = _bpm

    private var running = false
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Р—Р°РїСѓСЃРєР°РµС‚ СЃРёРјСѓР»СЏС†РёСЋ Р°РЅР°Р»РёР·Р° РїСѓР»СЊСЃР°.
     */
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

    /**
     * РћСЃС‚Р°РЅР°РІР»РёРІР°РµС‚ СЃРёРјСѓР»СЏС†РёСЋ Р°РЅР°Р»РёР·Р° РїСѓР»СЊСЃР°.
     */
    fun stop() {
        running = false
    }
}
