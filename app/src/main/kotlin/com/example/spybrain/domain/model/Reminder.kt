package com.example.spybrain.domain.model

import java.time.LocalTime

/**
 * Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ РЅР°РїРѕРјРёРЅР°РЅРёСЏ.
 */
data class Reminder(
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РЅР°РїРѕРјРёРЅР°РЅРёСЏ. */
    val id: String,
    /** РќР°Р·РІР°РЅРёРµ РЅР°РїРѕРјРёРЅР°РЅРёСЏ. */
    val title: String,
    /** РћРїРёСЃР°РЅРёРµ РЅР°РїРѕРјРёРЅР°РЅРёСЏ. */
    val description: String?,
    /** Р’СЂРµРјСЏ РЅР°РїРѕРјРёРЅР°РЅРёСЏ. */
    val time: Long,
    /** РўРёРї РЅР°РїРѕРјРёРЅР°РЅРёСЏ. */
    val type: ReminderType,
    /** Р”РЅРё РЅРµРґРµР»Рё. */
    val daysOfWeek: List<Int>,
    /** Р’РєР»СЋС‡РµРЅРѕ Р»Рё РЅР°РїРѕРјРёРЅР°РЅРёРµ. */
    val isEnabled: Boolean,
    /** Р’РєР»СЋС‡РµРЅР° Р»Рё РІРёР±СЂР°С†РёСЏ. */
    val vibrationEnabled: Boolean,
    /** Р’РєР»СЋС‡С‘РЅ Р»Рё Р·РІСѓРє. */
    val soundEnabled: Boolean,
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РєР°СЃС‚РѕРјРЅРѕРіРѕ РґРµР№СЃС‚РІРёСЏ. */
    val customActionId: String?
)
