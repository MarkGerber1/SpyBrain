package com.example.spybrain.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.spybrain.data.model.ScheduleEntity
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ РґРѕСЃС‚СѓРїР° Рє СЂР°СЃРїРёСЃР°РЅРёСЏРј РІ Р±Р°Р·Рµ РґР°РЅРЅС‹С….
 */
@Dao
interface ScheduleDao {

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РІСЃРµ СЂР°СЃРїРёСЃР°РЅРёСЏ.
     * @return Flow СЃРѕ СЃРїРёСЃРєРѕРј СЂР°СЃРїРёСЃР°РЅРёР№.
     */
    @Query("SELECT * FROM schedules")
    fun getAll(): Flow<List<ScheduleEntity>>

    /**
     * РџРѕР»СѓС‡РёС‚СЊ СЂР°СЃРїРёСЃР°РЅРёРµ РїРѕ id.
     * @param id РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ СЂР°СЃРїРёСЃР°РЅРёСЏ.
     * @return РЎСѓС‰РЅРѕСЃС‚СЊ СЂР°СЃРїРёСЃР°РЅРёСЏ РёР»Рё null.
     */
    @Query("SELECT * FROM schedules WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ScheduleEntity?

    /**
     * Р’СЃС‚Р°РІРёС‚СЊ СЂР°СЃРїРёСЃР°РЅРёРµ.
     * @param schedule РЎСѓС‰РЅРѕСЃС‚СЊ СЂР°СЃРїРёСЃР°РЅРёСЏ.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleEntity)

    /**
     * РћР±РЅРѕРІРёС‚СЊ СЂР°СЃРїРёСЃР°РЅРёРµ.
     * @param schedule РЎСѓС‰РЅРѕСЃС‚СЊ СЂР°СЃРїРёСЃР°РЅРёСЏ.
     */
    @Update
    suspend fun update(schedule: ScheduleEntity)

    /**
     * РЈРґР°Р»РёС‚СЊ СЂР°СЃРїРёСЃР°РЅРёРµ РїРѕ id.
     * @param id РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ СЂР°СЃРїРёСЃР°РЅРёСЏ.
     */
    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteById(id: String)
}
