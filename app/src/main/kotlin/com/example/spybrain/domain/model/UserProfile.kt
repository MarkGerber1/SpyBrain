package com.example.spybrain.domain.model

import java.time.LocalDateTime

/**
 * Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ РїСЂРѕС„РёР»СЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
data class UserProfile(
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ. */
    val id: String = "default_user",
    /** РРјСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ. */
    val name: String = "",
    /** Email РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ. */
    val email: String = "",
    /** URI Р°РІР°С‚Р°СЂР° РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ. */
    val avatarUri: String? = null,
    /** РћР±С‰РµРµ РєРѕР»РёС‡РµСЃС‚РІРѕ РѕС‡РєРѕРІ. */
    val totalPoints: Int = 0,
    /** РЈСЂРѕРІРµРЅСЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ. */
    val userLevel: Int = 1,
    /** Р’СЂРµРјСЏ СЃРѕР·РґР°РЅРёСЏ РїСЂРѕС„РёР»СЏ. */
    val createdAt: LocalDateTime = LocalDateTime.now(),
    /** Р’СЂРµРјСЏ РїРѕСЃР»РµРґРЅРµР№ Р°РєС‚РёРІРЅРѕСЃС‚Рё. */
    val lastActiveAt: LocalDateTime = LocalDateTime.now(),
    /** РќР°СЃС‚СЂРѕР№РєРё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ. */
    val settings: Map<String, String> = emptyMap()
)
