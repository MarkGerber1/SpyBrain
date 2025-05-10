package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Achievement
import kotlinx.coroutines.flow.Flow

interface AchievementsRepository {
    /** Получение списка достижений */
    fun getAchievements(): Flow<List<Achievement>>

    /** Разблокировать достижение по id с указанием времени */
    suspend fun unlockAchievement(id: String, unlockedAt: Long)
} 