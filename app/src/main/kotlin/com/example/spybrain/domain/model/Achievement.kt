package com.example.spybrain.domain.model

import java.io.Serializable

/**
 * Модель достижения со статусом и датой получения.
 */
data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconResId: Int = 0,
    val type: AchievementType = AchievementType.GENERAL,
    val points: Int = 0,
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val requiredValue: Int = 100,
    val unlockedAt: Long? = null
) : Serializable 