package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.CustomBreathingPattern

/**
 * Entity для хранения пользовательских шаблонов дыхания.
 */
@Entity(tableName = "custom_breathing_patterns")
data class CustomBreathingPatternEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val inhaleSeconds: Int,
    val holdAfterInhaleSeconds: Int,
    val exhaleSeconds: Int,
    val holdAfterExhaleSeconds: Int,
    val totalCycles: Int
)

/** Преобразование Entity в доменную модель */
fun CustomBreathingPatternEntity.toDomain(): CustomBreathingPattern = CustomBreathingPattern(
    id = id,
    name = name,
    description = description,
    inhaleSeconds = inhaleSeconds,
    holdAfterInhaleSeconds = holdAfterInhaleSeconds,
    exhaleSeconds = exhaleSeconds,
    holdAfterExhaleSeconds = holdAfterExhaleSeconds,
    totalCycles = totalCycles
)

/** Преобразование доменной модели в Entity */
fun CustomBreathingPattern.toEntity(): CustomBreathingPatternEntity = CustomBreathingPatternEntity(
    id = id,
    name = name,
    description = description,
    inhaleSeconds = inhaleSeconds,
    holdAfterInhaleSeconds = holdAfterInhaleSeconds,
    exhaleSeconds = exhaleSeconds,
    holdAfterExhaleSeconds = holdAfterExhaleSeconds,
    totalCycles = totalCycles
) 