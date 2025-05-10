package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.Achievement

/** Сущность достижения для Room */
@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

/** Преобразование Room-сущности в доменную модель */
fun AchievementEntity.toDomain(): Achievement = Achievement(
    id = id,
    title = title,
    description = description,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt
)

/** Преобразование доменной модели в Room-сущность */
fun Achievement.toEntity(): AchievementEntity = AchievementEntity(
    id = id,
    title = title,
    description = description,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt
) 