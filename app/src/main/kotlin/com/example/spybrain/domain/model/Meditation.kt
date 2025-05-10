package com.example.spybrain.domain.model

data class Meditation(
    val id: String,
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val audioUrl: String,
    val category: String // e.g., "Mindfulness", "Sleep"
) 