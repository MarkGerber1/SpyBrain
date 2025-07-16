package com.example.spybrain.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spybrain.data.model.BreathingSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ РґРѕСЃС‚СѓРїР° Рє СЃРµСЃСЃРёСЏРј РґС‹С…Р°РЅРёСЏ РІ Р±Р°Р·Рµ РґР°РЅРЅС‹С….
 */
@Dao
interface BreathingSessionDao {
    /**
     * Р’СЃС‚Р°РІРёС‚СЊ СЃРµСЃСЃРёСЋ РґС‹С…Р°РЅРёСЏ.
     * @param session РЎСѓС‰РЅРѕСЃС‚СЊ СЃРµСЃСЃРёРё.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: BreathingSessionEntity)

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РІСЃРµ СЃРµСЃСЃРёРё РґС‹С…Р°РЅРёСЏ.
     * @return Flow СЃРѕ СЃРїРёСЃРєРѕРј СЃРµСЃСЃРёР№.
     */
    @Query("SELECT * FROM breathing_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<BreathingSessionEntity>>
}
