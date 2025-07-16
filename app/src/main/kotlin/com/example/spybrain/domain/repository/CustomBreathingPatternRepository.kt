package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.CustomBreathingPattern
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёС… РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
 */
interface CustomBreathingPatternRepository {
    /**
     * РџРѕР»СѓС‡РёС‚СЊ РІСЃРµ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёРµ РїР°С‚С‚РµСЂРЅС‹.
     * @return РЎРїРёСЃРѕРє РїР°С‚С‚РµСЂРЅРѕРІ.
     */
    fun getAll(): List<CustomBreathingPattern>

    /**
     * Р”РѕР±Р°РІРёС‚СЊ РЅРѕРІС‹Р№ РїР°С‚С‚РµСЂРЅ.
     * @param pattern РќРѕРІС‹Р№ РїР°С‚С‚РµСЂРЅ.
     */
    fun add(pattern: CustomBreathingPattern)

    /**
     * РЈРґР°Р»РёС‚СЊ РїР°С‚С‚РµСЂРЅ РїРѕ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂСѓ.
     * @param id РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїР°С‚С‚РµСЂРЅР°.
     */
    fun delete(id: Long)
}
