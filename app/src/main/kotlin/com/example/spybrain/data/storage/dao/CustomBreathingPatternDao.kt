package com.example.spybrain.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.spybrain.data.model.CustomBreathingPatternEntity

/**
 * РРЅС‚РµСЂС„РµР№СЃ РґРѕСЃС‚СѓРїР° Рє РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёРј РґС‹С…Р°С‚РµР»СЊРЅС‹Рј РїР°С‚С‚РµСЂРЅР°Рј РІ Р±Р°Р·Рµ РґР°РЅРЅС‹С….
 */
@Dao
interface CustomBreathingPatternDao {
    /**
     * РџРѕР»СѓС‡РёС‚СЊ РІСЃРµ РїР°С‚С‚РµСЂРЅС‹.
     * @return Flow СЃРѕ СЃРїРёСЃРєРѕРј РїР°С‚С‚РµСЂРЅРѕРІ.
     */
    @Query("SELECT * FROM custom_breathing_patterns")
    fun getAllPatterns(): Flow<List<CustomBreathingPatternEntity>>

    /**
     * Р’СЃС‚Р°РІРёС‚СЊ РїР°С‚С‚РµСЂРЅ.
     * @param pattern РџР°С‚С‚РµСЂРЅ РґР»СЏ РІСЃС‚Р°РІРєРё.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPattern(pattern: CustomBreathingPatternEntity)

    /**
     * РЈРґР°Р»РёС‚СЊ РїР°С‚С‚РµСЂРЅ.
     * @param pattern РџР°С‚С‚РµСЂРЅ РґР»СЏ СѓРґР°Р»РµРЅРёСЏ.
     */
    @Delete
    suspend fun deletePattern(pattern: CustomBreathingPatternEntity)
}
