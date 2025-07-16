package com.example.spybrain.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spybrain.data.model.AchievementEntity
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ РґРѕСЃС‚СѓРїР° Рє РґРѕСЃС‚РёР¶РµРЅРёСЏРј РІ Р±Р°Р·Рµ РґР°РЅРЅС‹С….
 */
@Dao
interface AchievementDao {
    /**
     * РџРѕР»СѓС‡РёС‚СЊ РІСЃРµ РґРѕСЃС‚РёР¶РµРЅРёСЏ.
     * @return Flow СЃРѕ СЃРїРёСЃРєРѕРј РґРѕСЃС‚РёР¶РµРЅРёР№.
     */
    @Query("SELECT * FROM achievements")
    fun getAll(): Flow<List<AchievementEntity>>

    /**
     * Р’СЃС‚Р°РІРёС‚СЊ РґРѕСЃС‚РёР¶РµРЅРёРµ.
     * @param entity РЎСѓС‰РЅРѕСЃС‚СЊ РґРѕСЃС‚РёР¶РµРЅРёСЏ.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AchievementEntity)
}
