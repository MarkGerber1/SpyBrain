package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.BreathingPattern
import com.example.spybrain.domain.model.Session
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ Рё СЃРµСЃСЃРёР№.
 */
interface BreathingRepository {
    /**
     * РџРѕР»СѓС‡РёС‚СЊ СЃРїРёСЃРѕРє РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
     * @return РЎРїРёСЃРѕРє РїР°С‚С‚РµСЂРЅРѕРІ.
     */
    fun getBreathingPatterns(): Flow<List<BreathingPattern>>

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РїР°С‚С‚РµСЂРЅ РїРѕ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂСѓ.
     * @param id РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїР°С‚С‚РµСЂРЅР°.
     * @return РџР°С‚С‚РµСЂРЅ РёР»Рё null.
     */
    fun getBreathingPatternById(id: String): Flow<BreathingPattern?>

    /**
     * РћС‚СЃР»РµР¶РёРІР°С‚СЊ РґС‹С…Р°С‚РµР»СЊРЅСѓСЋ СЃРµСЃСЃРёСЋ.
     * @param session РЎРµСЃСЃРёСЏ РґС‹С…Р°РЅРёСЏ.
     */
    suspend fun trackBreathingSession(session: Session)
}
