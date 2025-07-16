package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.domain.model.Session
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ РјРµРґРёС‚Р°С†РёР№.
 */
interface MeditationRepository {
    /**
     * РџРѕР»СѓС‡РёС‚СЊ СЃРїРёСЃРѕРє РјРµРґРёС‚Р°С†РёР№.
     * @return РЎРїРёСЃРѕРє РјРµРґРёС‚Р°С†РёР№.
     */
    fun getMeditations(): Flow<List<Meditation>>

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РјРµРґРёС‚Р°С†РёСЋ РїРѕ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂСѓ.
     * @param id РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РјРµРґРёС‚Р°С†РёРё.
     * @return РњРµРґРёС‚Р°С†РёСЏ РёР»Рё null.
     */
    fun getMeditationById(id: String): Flow<Meditation?>

    /**
     * РћС‚СЃР»РµР¶РёРІР°С‚СЊ СЃРµСЃСЃРёСЋ РјРµРґРёС‚Р°С†РёРё.
     * @param session РЎРµСЃСЃРёСЏ РјРµРґРёС‚Р°С†РёРё.
     */
    suspend fun trackMeditationSession(session: Session)
}
