package com.example.spybrain.domain.model

import java.util.Date

/**
 * Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ СЃРµСЃСЃРёРё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
data class Session(
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ СЃРµСЃСЃРёРё. */
    val id: String,
    /** РўРёРї СЃРµСЃСЃРёРё. */
    val type: SessionType, // Meditation or Breathing
    /** Р’СЂРµРјСЏ РЅР°С‡Р°Р»Р°. */
    val startTime: Date,
    /** Р’СЂРµРјСЏ РѕРєРѕРЅС‡Р°РЅРёСЏ. */
    val endTime: Date,
    /** Р”Р»РёС‚РµР»СЊРЅРѕСЃС‚СЊ РІ СЃРµРєСѓРЅРґР°С…. */
    val durationSeconds: Long,
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ СЃРІСЏР·Р°РЅРЅРѕРіРѕ СЌР»РµРјРµРЅС‚Р°. */
    val relatedItemId: String? = null // ID of Meditation or BreathingPattern
)

/**
 * РџРµСЂРµС‡РёСЃР»РµРЅРёРµ С‚РёРїРѕРІ СЃРµСЃСЃРёР№.
 */
enum class SessionType {
    /** РЎРµСЃСЃРёСЏ РґС‹С…Р°РЅРёСЏ. */
    BREATHING,
    /** РЎРµСЃСЃРёСЏ РјРµРґРёС‚Р°С†РёРё. */
    MEDITATION
}
