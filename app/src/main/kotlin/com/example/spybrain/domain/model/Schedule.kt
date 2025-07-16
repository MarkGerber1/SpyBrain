package com.example.spybrain.domain.model

import java.time.LocalTime

/**
 * Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ СЂР°СЃРїРёСЃР°РЅРёСЏ.
 */
data class Schedule(
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ СЂР°СЃРїРёСЃР°РЅРёСЏ. */
    val id: String,
    /** РќР°Р·РІР°РЅРёРµ СЂР°СЃРїРёСЃР°РЅРёСЏ. */
    val title: String,
    /** РћРїРёСЃР°РЅРёРµ СЂР°СЃРїРёСЃР°РЅРёСЏ. */
    val description: String?,
    /** Р’СЂРµРјСЏ РЅР°С‡Р°Р»Р°. */
    val startTime: Long,
    /** Р’СЂРµРјСЏ РѕРєРѕРЅС‡Р°РЅРёСЏ. */
    val endTime: Long,
    /** РўРёРї СЂР°СЃРїРёСЃР°РЅРёСЏ. */
    val type: ScheduleType,
    /** Р”РЅРё РЅРµРґРµР»Рё. */
    val daysOfWeek: List<Int>,
    /** Р’РєР»СЋС‡РµРЅРѕ Р»Рё СЂР°СЃРїРёСЃР°РЅРёРµ. */
    val isEnabled: Boolean,
    /** Р’РєР»СЋС‡РµРЅРѕ Р»Рё СѓРІРµРґРѕРјР»РµРЅРёРµ. */
    val notificationEnabled: Boolean,
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ СЃРІСЏР·Р°РЅРЅРѕРіРѕ СЌР»РµРјРµРЅС‚Р°. */
    val itemId: String?,
    /** РЎРїРёСЃРѕРє РЅР°РїРѕРјРёРЅР°РЅРёР№. */
    val reminders: List<Reminder>
) {
    /**
     * Companion object Schedule (Р°РІС‚РѕСЃРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ).
     */
    companion object {
        /**
         * РљРѕРЅСЃС‚Р°РЅС‚Р° РґР»СЏ РІСЃРµС… РґРЅРµР№ РЅРµРґРµР»Рё (РїРЅ-РІСЃ).
         */
        const val WEEKDAYS = 0b1111111 // Р’СЃРµ РґРЅРё РЅРµРґРµР»Рё (РїРЅ-РІСЃ)
    }
}
