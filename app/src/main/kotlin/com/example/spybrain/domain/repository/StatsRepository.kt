package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.Stats
import com.example.spybrain.domain.model.BreathingSession
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ СЃС‚Р°С‚РёСЃС‚РёРєРё.
 */
interface StatsRepository {
    /**
     * РџРѕР»СѓС‡РёС‚СЊ РѕР±С‰СѓСЋ СЃС‚Р°С‚РёСЃС‚РёРєСѓ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РћР±С‰Р°СЏ СЃС‚Р°С‚РёСЃС‚РёРєР°.
     */
    fun getOverallStats(): Flow<Stats>

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РёСЃС‚РѕСЂРёСЋ СЃРµСЃСЃРёР№.
     * @return РЎРїРёСЃРѕРє СЃРµСЃСЃРёР№.
     */
    fun getSessionHistory(): Flow<List<Session>>

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РёСЃС‚РѕСЂРёСЋ РґС‹С…Р°С‚РµР»СЊРЅС‹С… СЃРµСЃСЃРёР№.
     * @return РЎРїРёСЃРѕРє РґС‹С…Р°С‚РµР»СЊРЅС‹С… СЃРµСЃСЃРёР№.
     */
    fun getBreathingHistory(): Flow<List<BreathingSession>>

    /**
     * РЎРѕС…СЂР°РЅРёС‚СЊ СЃРµСЃСЃРёСЋ.
     * @param session РЎРѕС…СЂР°РЅСЏРµРјР°СЏ СЃРµСЃСЃРёСЏ.
     */
    suspend fun saveSession(session: Session)

    /**
     * РЎРѕС…СЂР°РЅРёС‚СЊ РґС‹С…Р°С‚РµР»СЊРЅСѓСЋ СЃРµСЃСЃРёСЋ.
     * @param session РЎРѕС…СЂР°РЅСЏРµРјР°СЏ РґС‹С…Р°С‚РµР»СЊРЅР°СЏ СЃРµСЃСЃРёСЏ.
     */
    suspend fun saveBreathingSession(session: BreathingSession)
}
