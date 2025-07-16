package com.example.spybrain.domain.model

/**
 * Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёС… РЅР°СЃС‚СЂРѕРµРє.
 */
data class Settings(
    /** РўРµРјР° РѕС„РѕСЂРјР»РµРЅРёСЏ. */
    val theme: Theme = Theme.SYSTEM
    // Add other settings fields here, e.g.:
    // val language: String = "system",
    // val backgroundMusicEnabled: Boolean = true
)
