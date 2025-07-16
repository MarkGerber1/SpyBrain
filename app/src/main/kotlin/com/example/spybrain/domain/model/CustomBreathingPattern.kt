package com.example.spybrain.domain.model

import java.util.UUID

/**
 * Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРѕРіРѕ РґС‹С…Р°С‚РµР»СЊРЅРѕРіРѕ РїР°С‚С‚РµСЂРЅР°.
 */
data class CustomBreathingPattern(
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїР°С‚С‚РµСЂРЅР°. */
    val id: Long,
    /** РќР°Р·РІР°РЅРёРµ РїР°С‚С‚РµСЂРЅР°. */
    val name: String,
    /** РћРїРёСЃР°РЅРёРµ РїР°С‚С‚РµСЂРЅР°. */
    val description: String?,
    /** Р”Р»РёС‚РµР»СЊРЅРѕСЃС‚СЊ РІРґРѕС…Р° (СЃРµРє). */
    val inhaleSeconds: Int,
    /** Р—Р°РґРµСЂР¶РєР° РїРѕСЃР»Рµ РІРґРѕС…Р° (СЃРµРє). */
    val holdAfterInhaleSeconds: Int,
    /** Р”Р»РёС‚РµР»СЊРЅРѕСЃС‚СЊ РІС‹РґРѕС…Р° (СЃРµРє). */
    val exhaleSeconds: Int,
    /** Р—Р°РґРµСЂР¶РєР° РїРѕСЃР»Рµ РІС‹РґРѕС…Р° (СЃРµРє). */
    val holdAfterExhaleSeconds: Int,
    /** РљРѕР»РёС‡РµСЃС‚РІРѕ С†РёРєР»РѕРІ. */
    val totalCycles: Int
)
