package com.example.spybrain.domain.model

import java.time.LocalDateTime

// Модель для хранения завершённых сессий дыхания
data class BreathingSession(
    val patternName: String,
    val durationSeconds: Int,
    val completed: Boolean,
    val timestamp: LocalDateTime = LocalDateTime.now()
) 