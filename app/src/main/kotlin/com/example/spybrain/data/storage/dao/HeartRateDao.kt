package com.example.spybrain.data.storage.dao

import androidx.room.*
import com.example.spybrain.data.storage.model.HeartRateMeasurement
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ РґРѕСЃС‚СѓРїР° Рє РёР·РјРµСЂРµРЅРёСЏРј РїСѓР»СЊСЃР° РІ Р±Р°Р·Рµ РґР°РЅРЅС‹С….
 */
@Dao
interface HeartRateDao {

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РІСЃРµ РёР·РјРµСЂРµРЅРёСЏ РїСѓР»СЊСЃР°.
     * @return РЎРїРёСЃРѕРє РёР·РјРµСЂРµРЅРёР№.
     */
    @Query("SELECT * FROM heart_rate_measurements ORDER BY timestamp DESC")
    suspend fun getAllMeasurements(): List<HeartRateMeasurement>

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РІСЃРµ РёР·РјРµСЂРµРЅРёСЏ РїСѓР»СЊСЃР° РєР°Рє Flow.
     * @return Flow СЃРѕ СЃРїРёСЃРєРѕРј РёР·РјРµСЂРµРЅРёР№.
     */
    @Query("SELECT * FROM heart_rate_measurements ORDER BY timestamp DESC")
    fun getAllMeasurementsFlow(): Flow<List<HeartRateMeasurement>>

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РёР·РјРµСЂРµРЅРёСЏ СЃ РѕРїСЂРµРґРµР»С‘РЅРЅРѕР№ РґР°С‚С‹.
     * @param startDate РќР°С‡Р°Р»СЊРЅР°СЏ РґР°С‚Р° (timestamp).
     * @return РЎРїРёСЃРѕРє РёР·РјРµСЂРµРЅРёР№.
     */
    @Query("SELECT * FROM heart_rate_measurements WHERE timestamp >= :startDate ORDER BY timestamp DESC")
    suspend fun getMeasurementsFromDate(startDate: Long): List<HeartRateMeasurement>

    /**
     * РџРѕР»СѓС‡РёС‚СЊ СЃСЂРµРґРЅРёР№ РїСѓР»СЊСЃ Р·Р° РґРµРЅСЊ.
     * @param startOfDay РќР°С‡Р°Р»Рѕ РґРЅСЏ (timestamp).
     * @param endOfDay РљРѕРЅРµС† РґРЅСЏ (timestamp).
     * @return РЎСЂРµРґРЅРёР№ РїСѓР»СЊСЃ РёР»Рё null.
     */
    @Query("SELECT AVG(heartRate) FROM heart_rate_measurements WHERE timestamp >= :startOfDay AND timestamp < :endOfDay")
    suspend fun getAverageHeartRateForDate(startOfDay: Long, endOfDay: Long): Float?

    /**
     * Р’СЃС‚Р°РІРёС‚СЊ РёР·РјРµСЂРµРЅРёРµ РїСѓР»СЊСЃР°.
     * @param measurement РЎСѓС‰РЅРѕСЃС‚СЊ РёР·РјРµСЂРµРЅРёСЏ.
     */
    @Insert
    suspend fun insertMeasurement(measurement: HeartRateMeasurement)

    /**
     * РЈРґР°Р»РёС‚СЊ РёР·РјРµСЂРµРЅРёРµ РїСѓР»СЊСЃР°.
     * @param measurement РЎСѓС‰РЅРѕСЃС‚СЊ РёР·РјРµСЂРµРЅРёСЏ.
     */
    @Delete
    suspend fun deleteMeasurement(measurement: HeartRateMeasurement)

    /**
     * РЈРґР°Р»РёС‚СЊ РІСЃРµ РёР·РјРµСЂРµРЅРёСЏ РїСѓР»СЊСЃР°.
     */
    @Query("DELETE FROM heart_rate_measurements")
    suspend fun deleteAllMeasurements()

    /**
     * РЈРґР°Р»РёС‚СЊ СЃС‚Р°СЂС‹Рµ РёР·РјРµСЂРµРЅРёСЏ РїСѓР»СЊСЃР°.
     * @param date Р”Р°С‚Р° (timestamp).
     */
    @Query("DELETE FROM heart_rate_measurements WHERE timestamp < :date")
    suspend fun deleteOldMeasurements(date: Long)
}
