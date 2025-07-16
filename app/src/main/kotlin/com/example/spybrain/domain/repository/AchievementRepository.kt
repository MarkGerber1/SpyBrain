package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.domain.model.AchievementType
import com.example.spybrain.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ РґР»СЏ СЂР°Р±РѕС‚С‹ СЃ РґРѕСЃС‚РёР¶РµРЅРёСЏРјРё Рё РїСЂРѕС„РёР»РµРј РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
interface AchievementRepository {

    /**
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє РІСЃРµС… РґРѕСЃС‚РёР¶РµРЅРёР№.
     * @return РЎРїРёСЃРѕРє РґРѕСЃС‚РёР¶РµРЅРёР№.
     */
    fun getAllAchievements(): Flow<List<Achievement>>

    /**
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє СЂР°Р·Р±Р»РѕРєРёСЂРѕРІР°РЅРЅС‹С… РґРѕСЃС‚РёР¶РµРЅРёР№.
     * @return РЎРїРёСЃРѕРє РґРѕСЃС‚РёР¶РµРЅРёР№.
     */
    fun getUnlockedAchievements(): Flow<List<Achievement>>

    /**
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє РґРѕСЃС‚РёР¶РµРЅРёР№ СѓРєР°Р·Р°РЅРЅРѕРіРѕ С‚РёРїР°.
     * @param type РўРёРї РґРѕСЃС‚РёР¶РµРЅРёСЏ.
     * @return РЎРїРёСЃРѕРє РґРѕСЃС‚РёР¶РµРЅРёР№.
     */
    fun getAchievementsByType(type: AchievementType): Flow<List<Achievement>>

    /**
     * РџРѕР»СѓС‡Р°РµС‚ РґРѕСЃС‚РёР¶РµРЅРёРµ РїРѕ ID.
     * @param id ID РґРѕСЃС‚РёР¶РµРЅРёСЏ.
     * @return Р”РѕСЃС‚РёР¶РµРЅРёРµ РёР»Рё null.
     */
    suspend fun getAchievementById(id: String): Achievement?

    /**
     * РџСЂРѕРІРµСЂСЏРµС‚ Рё РѕР±РЅРѕРІР»СЏРµС‚ РґРѕСЃС‚РёР¶РµРЅРёСЏ РЅР° РѕСЃРЅРѕРІРµ РЅРѕРІС‹С… РґР°РЅРЅС‹С….
     * @param meditationMinutes РћР±С‰РµРµ РІСЂРµРјСЏ РјРµРґРёС‚Р°С†РёР№ РІ РјРёРЅСѓС‚Р°С….
     * @param breathingMinutes РћР±С‰РµРµ РІСЂРµРјСЏ РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїСЂР°РєС‚РёРє РІ РјРёРЅСѓС‚Р°С….
     * @param sessionsCount РћР±С‰РµРµ РєРѕР»РёС‡РµСЃС‚РІРѕ СЃРµСЃСЃРёР№.
     * @param currentStreak РўРµРєСѓС‰Р°СЏ СЃРµСЂРёСЏ РґРЅРµР№.
     * @param longestStreak РЎР°РјР°СЏ РґРѕР»РіР°СЏ СЃРµСЂРёСЏ РґРЅРµР№.
     * @return РЎРїРёСЃРѕРє РѕР±РЅРѕРІР»С‘РЅРЅС‹С… РґРѕСЃС‚РёР¶РµРЅРёР№.
     */
    suspend fun checkAndUpdateAchievements(
        meditationMinutes: Int,
        breathingMinutes: Int,
        sessionsCount: Int,
        currentStreak: Int,
        longestStreak: Int
    ): List<Achievement>

    /**
     * Р Р°Р·Р±Р»РѕРєРёСЂСѓРµС‚ РґРѕСЃС‚РёР¶РµРЅРёРµ.
     * @param id ID РґРѕСЃС‚РёР¶РµРЅРёСЏ.
     */
    suspend fun unlockAchievement(id: String)

    /**
     * РћР±РЅРѕРІР»СЏРµС‚ РїСЂРѕРіСЂРµСЃСЃ РґРѕСЃС‚РёР¶РµРЅРёСЏ.
     * @param id ID РґРѕСЃС‚РёР¶РµРЅРёСЏ.
     * @param progress РќРѕРІРѕРµ Р·РЅР°С‡РµРЅРёРµ РїСЂРѕРіСЂРµСЃСЃР° (РѕС‚ 0.0 РґРѕ 1.0).
     */
    suspend fun updateAchievementProgress(id: String, progress: Float)

    /**
     * РџРѕР»СѓС‡Р°РµС‚ РїСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РџСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    fun getUserProfile(): Flow<UserProfile>

    /**
     * РћР±РЅРѕРІР»СЏРµС‚ РёРјСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @param name РќРѕРІРѕРµ РёРјСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    suspend fun updateUserName(name: String)

    /**
     * РћР±РЅРѕРІР»СЏРµС‚ Р°РІР°С‚Р°СЂ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @param avatarUrl URL РЅРѕРІРѕРіРѕ Р°РІР°С‚Р°СЂР°.
     */
    suspend fun updateUserAvatar(avatarUrl: String)

    /**
     * РџРѕР»СѓС‡Р°РµС‚ РѕР±С‰РµРµ РєРѕР»РёС‡РµСЃС‚РІРѕ РѕС‡РєРѕРІ.
     * @return РљРѕР»РёС‡РµСЃС‚РІРѕ РѕС‡РєРѕРІ.
     */
    fun getTotalPoints(): Flow<Int>

    /**
     * Р Р°СЃСЃС‡РёС‚С‹РІР°РµС‚ СѓСЂРѕРІРµРЅСЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ РЅР° РѕСЃРЅРѕРІРµ РѕС‡РєРѕРІ.
     * @param points РљРѕР»РёС‡РµСЃС‚РІРѕ РѕС‡РєРѕРІ.
     * @return РЈСЂРѕРІРµРЅСЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    suspend fun calculateUserLevel(points: Int): Int
}
