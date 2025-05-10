package com.example.spybrain.domain.model

data class BreathingPattern(
    val id: String,
    val name: String,
    val voicePrompt: String,
    val description: String,
    val inhaleSeconds: Int,
    val holdAfterInhaleSeconds: Int,
    val exhaleSeconds: Int,
    val holdAfterExhaleSeconds: Int,
    val totalCycles: Int
) 