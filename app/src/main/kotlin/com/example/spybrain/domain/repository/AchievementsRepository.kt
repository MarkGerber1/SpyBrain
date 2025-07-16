package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Achievement
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ РґР»СЏ СЂР°Р±РѕС‚С‹ СЃ РґРѕСЃС‚РёР¶РµРЅРёСЏРјРё.
 */
interface AchievementsRepository {
    /**
     * РџРѕР»СѓС‡РёС‚СЊ РІСЃРµ РґРѕСЃС‚РёР¶РµРЅРёСЏ.
     * @return РЎРїРёСЃРѕРє РґРѕСЃС‚РёР¶РµРЅРёР№.
     */
    fun getAllAchievements(): List<Achievement>

    /**
     * РџСЂРѕРІРµСЂРёС‚СЊ РґРѕСЃС‚РёР¶РµРЅРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @param userId РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РЎРїРёСЃРѕРє РЅРѕРІС‹С… РґРѕСЃС‚РёР¶РµРЅРёР№.
     */
    fun checkAchievements(userId: String): List<Achievement>

    /**
     * РџРѕР»СѓС‡РµРЅРёРµ СЃРїРёСЃРєР° РґРѕСЃС‚РёР¶РµРЅРёР№.
     */
    fun getAchievements(): Flow<List<Achievement>>

    /**
     * Р Р°Р·Р±Р»РѕРєРёСЂРѕРІР°С‚СЊ РґРѕСЃС‚РёР¶РµРЅРёРµ РїРѕ id СЃ СѓРєР°Р·Р°РЅРёРµРј РІСЂРµРјРµРЅРё.
     */
    suspend fun unlockAchievement(id: String, unlockedAt: Long)
}
