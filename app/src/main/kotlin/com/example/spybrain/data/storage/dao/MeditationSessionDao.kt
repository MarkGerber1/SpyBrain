package com.example.spybrain.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spybrain.data.model.MeditationSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ РґРѕСЃС‚СѓРїР° Рє СЃРµСЃСЃРёСЏРј РјРµРґРёС‚Р°С†РёРё РІ Р±Р°Р·Рµ РґР°РЅРЅС‹С….
 */
@Dao
interface MeditationSessionDao {
    /**
     * Р’СЃС‚Р°РІРёС‚СЊ СЃРµСЃСЃРёСЋ РјРµРґРёС‚Р°С†РёРё.
     * @param session РЎСѓС‰РЅРѕСЃС‚СЊ СЃРµСЃСЃРёРё.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: MeditationSessionEntity)

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РІСЃРµ СЃРµСЃСЃРёРё РјРµРґРёС‚Р°С†РёРё.
     * @return Flow СЃРѕ СЃРїРёСЃРєРѕРј СЃРµСЃСЃРёР№.
     */
    @Query("SELECT * FROM meditation_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<MeditationSessionEntity>>
}
