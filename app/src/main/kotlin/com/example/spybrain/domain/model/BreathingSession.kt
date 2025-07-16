package com.example.spybrain.domain.model

import java.time.LocalDateTime

/**
 * Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ СЃРµСЃСЃРёРё РґС‹С…Р°РЅРёСЏ.
 */
data class BreathingSession(
    /** РќР°Р·РІР°РЅРёРµ РїР°С‚С‚РµСЂРЅР°. */
    val patternName: String,
    /** Р”Р»РёС‚РµР»СЊРЅРѕСЃС‚СЊ СЃРµСЃСЃРёРё РІ СЃРµРєСѓРЅРґР°С…. */
    val durationSeconds: Int,
    /** РџСЂРёР·РЅР°Рє Р·Р°РІРµСЂС€С‘РЅРЅРѕСЃС‚Рё. */
    val completed: Boolean,
    /** Р’СЂРµРјСЏ РїСЂРѕРІРµРґРµРЅРёСЏ СЃРµСЃСЃРёРё (timestamp). */
    val timestamp: Long
)
