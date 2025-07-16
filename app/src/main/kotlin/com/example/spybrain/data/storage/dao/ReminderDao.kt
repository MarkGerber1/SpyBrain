package com.example.spybrain.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.spybrain.data.model.ReminderEntity
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ РґРѕСЃС‚СѓРїР° Рє РЅР°РїРѕРјРёРЅР°РЅРёСЏРј РІ Р±Р°Р·Рµ РґР°РЅРЅС‹С….
 */
@Dao
interface ReminderDao {

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РІСЃРµ РЅР°РїРѕРјРёРЅР°РЅРёСЏ.
     * @return Flow СЃРѕ СЃРїРёСЃРєРѕРј РЅР°РїРѕРјРёРЅР°РЅРёР№.
     */
    @Query("SELECT * FROM reminders")
    fun getAll(): Flow<List<ReminderEntity>>

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РЅР°РїРѕРјРёРЅР°РЅРёРµ РїРѕ id.
     * @param id РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РЅР°РїРѕРјРёРЅР°РЅРёСЏ.
     * @return РЎСѓС‰РЅРѕСЃС‚СЊ РЅР°РїРѕРјРёРЅР°РЅРёСЏ РёР»Рё null.
     */
    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ReminderEntity?

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РЅР°РїРѕРјРёРЅР°РЅРёСЏ РїРѕ РґРЅСЋ РЅРµРґРµР»Рё.
     * @param dayOfWeek Р”РµРЅСЊ РЅРµРґРµР»Рё.
     * @return РЎРїРёСЃРѕРє РЅР°РїРѕРјРёРЅР°РЅРёР№.
     */
    @Query("SELECT * FROM reminders WHERE daysOfWeek & (1 << :dayOfWeek) > 0 AND isEnabled = 1")
    suspend fun getByDaysOfWeek(dayOfWeek: Int): List<ReminderEntity>

    /**
     * Р’СЃС‚Р°РІРёС‚СЊ РЅР°РїРѕРјРёРЅР°РЅРёРµ.
     * @param reminder РЎСѓС‰РЅРѕСЃС‚СЊ РЅР°РїРѕРјРёРЅР°РЅРёСЏ.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity)

    /**
     * РћР±РЅРѕРІРёС‚СЊ РЅР°РїРѕРјРёРЅР°РЅРёРµ.
     * @param reminder РЎСѓС‰РЅРѕСЃС‚СЊ РЅР°РїРѕРјРёРЅР°РЅРёСЏ.
     */
    @Update
    suspend fun update(reminder: ReminderEntity)

    /**
     * РЈРґР°Р»РёС‚СЊ РЅР°РїРѕРјРёРЅР°РЅРёРµ РїРѕ id.
     * @param id РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РЅР°РїРѕРјРёРЅР°РЅРёСЏ.
     */
    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: String)
}
