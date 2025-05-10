package com.example.spybrain.domain.model

import java.io.Serializable

/**
 * Модель достижения со статусом и датой получения.
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val unlockedAt: Long?
) : Serializable 