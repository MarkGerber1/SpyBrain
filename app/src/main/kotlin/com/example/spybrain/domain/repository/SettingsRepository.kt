package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Settings
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёС… РЅР°СЃС‚СЂРѕРµРє.
 */
interface SettingsRepository {
    /**
     * РџРѕР»СѓС‡РёС‚СЊ РЅР°СЃС‚СЂРѕР№РєРё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РќР°СЃС‚СЂРѕР№РєРё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    fun getSettings(): Flow<Settings>

    /**
     * РЎРѕС…СЂР°РЅРёС‚СЊ РЅР°СЃС‚СЂРѕР№РєРё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @param settings РќРѕРІС‹Рµ РЅР°СЃС‚СЂРѕР№РєРё.
     */
    suspend fun saveSettings(settings: Settings)
}
