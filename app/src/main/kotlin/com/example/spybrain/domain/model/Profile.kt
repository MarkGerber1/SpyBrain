package com.example.spybrain.domain.model

import java.util.Date

/**
 * РџСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 * @property userId РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 * @property name РРјСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 * @property email Email РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 * @property joinDate Р”Р°С‚Р° СЂРµРіРёСЃС‚СЂР°С†РёРё.
 * @property streakDays РљРѕР»РёС‡РµСЃС‚РІРѕ РґРЅРµР№ РІ СЃРµСЂРёРё.
 * @property avatarUrl URL Р°РІР°С‚Р°СЂР° РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
data class Profile(
    val userId: String,
    val name: String,
    val email: String, // Consider if email is needed/handled elsewhere
    val joinDate: Date,
    val streakDays: Int = 0,
    val avatarUrl: String? = null
)
