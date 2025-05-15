package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.domain.model.AchievementType
import com.example.spybrain.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с достижениями и профилем пользователя
 */
interface AchievementRepository {
    
    /**
     * Получает список всех достижений
     */
    fun getAllAchievements(): Flow<List<Achievement>>
    
    /**
     * Получает список разблокированных достижений
     */
    fun getUnlockedAchievements(): Flow<List<Achievement>>
    
    /**
     * Получает список достижений указанного типа
     * @param type Тип достижения
     */
    fun getAchievementsByType(type: AchievementType): Flow<List<Achievement>>
    
    /**
     * Получает достижение по ID
     * @param id ID достижения
     */
    suspend fun getAchievementById(id: String): Achievement?
    
    /**
     * Проверяет и обновляет достижения на основе новых данных
     * @param meditationMinutes Общее время медитаций в минутах
     * @param breathingMinutes Общее время дыхательных практик в минутах
     * @param sessionsCount Общее количество сессий
     * @param currentStreak Текущая серия дней
     * @param longestStreak Самая долгая серия дней
     */
    suspend fun checkAndUpdateAchievements(
        meditationMinutes: Int,
        breathingMinutes: Int,
        sessionsCount: Int,
        currentStreak: Int,
        longestStreak: Int
    ): List<Achievement>
    
    /**
     * Разблокирует достижение
     * @param id ID достижения
     */
    suspend fun unlockAchievement(id: String)
    
    /**
     * Обновляет прогресс достижения
     * @param id ID достижения
     * @param progress Новое значение прогресса (от 0.0 до 1.0)
     */
    suspend fun updateAchievementProgress(id: String, progress: Float)
    
    /**
     * Получает профиль пользователя
     */
    fun getUserProfile(): Flow<UserProfile>
    
    /**
     * Обновляет имя пользователя
     * @param name Новое имя пользователя
     */
    suspend fun updateUserName(name: String)
    
    /**
     * Обновляет аватар пользователя
     * @param avatarUrl URL нового аватара
     */
    suspend fun updateUserAvatar(avatarUrl: String)
    
    /**
     * Получает общее количество очков
     */
    fun getTotalPoints(): Flow<Int>
    
    /**
     * Рассчитывает уровень пользователя на основе очков
     * @param points Количество очков
     * @return Уровень пользователя
     */
    suspend fun calculateUserLevel(points: Int): Int
} 