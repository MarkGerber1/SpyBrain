package com.example.spybrain.domain.model

import java.util.Date

data class Session(
    val id: String,
    val type: SessionType, // Meditation or Breathing
    val startTime: Date,
    val endTime: Date,
    val durationSeconds: Long,
    val relatedItemId: String? = null // ID of Meditation or BreathingPattern
)

enum class SessionType {
    MEDITATION,
    BREATHING
} 