package com.example.spybrain.domain.model

data class Stats(
    val totalMeditationTimeSeconds: Long,
    val totalBreathingTimeSeconds: Long,
    val completedMeditationSessions: Int,
    val completedBreathingSessions: Int,
    val currentStreakDays: Int,
    val longestStreakDays: Int
    // Add more specific stats as needed
) 