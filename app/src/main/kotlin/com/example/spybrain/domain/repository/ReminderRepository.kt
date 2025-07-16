package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Reminder
import com.example.spybrain.domain.model.Schedule
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * РРЅС‚РµСЂС„РµР№СЃ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ РЅР°РїРѕРјРёРЅР°РЅРёР№.
 */
interface ReminderRepository {

    /**
     * РџРѕР»СѓС‡РёС‚СЊ СЃРїРёСЃРѕРє РЅР°РїРѕРјРёРЅР°РЅРёР№.
     * @return РЎРїРёСЃРѕРє РЅР°РїРѕРјРёРЅР°РЅРёР№.
     */
    fun getAllReminders(): Flow<List<Reminder>>

    /**
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє РІСЃРµС… СЂР°СЃРїРёСЃР°РЅРёР№.
     */
    fun getAllSchedules(): Flow<List<Schedule>>

    /**
     * Р”РѕР±Р°РІРёС‚СЊ РЅР°РїРѕРјРёРЅР°РЅРёРµ.
     * @param reminder РќРѕРІРѕРµ РЅР°РїРѕРјРёРЅР°РЅРёРµ.
     */
    suspend fun addReminder(reminder: Reminder)

    /**
     * РЈРґР°Р»РёС‚СЊ РЅР°РїРѕРјРёРЅР°РЅРёРµ РїРѕ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂСѓ.
     * @param id РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РЅР°РїРѕРјРёРЅР°РЅРёСЏ.
     */
    suspend fun deleteReminder(id: String)

    /**
     * РџРѕР»СѓС‡Р°РµС‚ РЅР°РїРѕРјРёРЅР°РЅРёРµ РїРѕ ID.
     * @param id ID РЅР°РїРѕРјРёРЅР°РЅРёСЏ.
     * @return РќР°РїРѕРјРёРЅР°РЅРёРµ РёР»Рё null, РµСЃР»Рё РЅРµ РЅР°Р№РґРµРЅРѕ.
     */
    suspend fun getReminderById(id: String): Reminder?

    /**
     * Р’РєР»СЋС‡Р°РµС‚ РёР»Рё РѕС‚РєР»СЋС‡Р°РµС‚ РЅР°РїРѕРјРёРЅР°РЅРёРµ.
     * @param id ID РЅР°РїРѕРјРёРЅР°РЅРёСЏ.
     * @param enabled РќРѕРІРѕРµ СЃРѕСЃС‚РѕСЏРЅРёРµ.
     */
    suspend fun setReminderEnabled(id: String, enabled: Boolean)

    /**
     * Р”РѕР±Р°РІР»СЏРµС‚ РЅРѕРІРѕРµ СЂР°СЃРїРёСЃР°РЅРёРµ.
     * @param schedule Р Р°СЃРїРёСЃР°РЅРёРµ РґР»СЏ РґРѕР±Р°РІР»РµРЅРёСЏ.
     */
    suspend fun addSchedule(schedule: Schedule)

    /**
     * РЈРґР°Р»СЏРµС‚ СЂР°СЃРїРёСЃР°РЅРёРµ РїРѕ ID.
     * @param id ID СЂР°СЃРїРёСЃР°РЅРёСЏ.
     */
    suspend fun deleteSchedule(id: String)

    /**
     * РџРѕР»СѓС‡Р°РµС‚ СЂР°СЃРїРёСЃР°РЅРёРµ РїРѕ ID.
     * @param id ID СЂР°СЃРїРёСЃР°РЅРёСЏ.
     * @return Р Р°СЃРїРёСЃР°РЅРёРµ РёР»Рё null, РµСЃР»Рё РЅРµ РЅР°Р№РґРµРЅРѕ.
     */
    suspend fun getScheduleById(id: String): Schedule?

    /**
     * Р’РєР»СЋС‡Р°РµС‚ РёР»Рё РѕС‚РєР»СЋС‡Р°РµС‚ СЂР°СЃРїРёСЃР°РЅРёРµ.
     * @param id ID СЂР°СЃРїРёСЃР°РЅРёСЏ.
     * @param enabled РќРѕРІРѕРµ СЃРѕСЃС‚РѕСЏРЅРёРµ.
     */
    suspend fun setScheduleEnabled(id: String, enabled: Boolean)
}
