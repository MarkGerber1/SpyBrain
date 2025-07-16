package com.example.spybrain.domain.model

import java.io.Serializable

/**
 * РџСЂРµРґСЃС‚Р°РІР»СЏРµС‚ РґРѕСЃС‚РёР¶РµРЅРёРµ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconResId: Int = 0,
    val type: AchievementType = AchievementType.GENERAL,
    val points: Int = 0,
    val isUnlocked: Boolean = false,
    val progress: Float = 0f,
    val requiredValue: Int = 100,
    val unlockedAt: Long? = null
) : Serializable
