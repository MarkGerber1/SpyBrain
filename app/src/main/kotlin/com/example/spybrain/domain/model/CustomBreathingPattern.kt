package com.example.spybrain.domain.model

import java.util.UUID

/**
 * Модель для хранения пользовательских шаблонов дыхания.
 */
data class CustomBreathingPattern(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String?,
    val inhaleSeconds: Int,
    val holdAfterInhaleSeconds: Int,
    val exhaleSeconds: Int,
    val holdAfterExhaleSeconds: Int,
    val totalCycles: Int
) 