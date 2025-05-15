package com.example.spybrain.data.repository

import com.example.spybrain.R
import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.domain.model.AchievementType

/**
 * Предопределенные достижения для приложения
 */
object PredefinedAchievements {
    
    val ALL_ACHIEVEMENTS = listOf(
        // Достижения за медитацию
        Achievement(
            id = "meditation_beginner",
            title = "Начинающий медитатор",
            description = "Проведите 10 минут в медитации",
            iconResId = R.drawable.ic_achievement_meditation_beginner,
            type = AchievementType.MEDITATION,
            points = 10,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 10,
            unlockedAt = null
        ),
        Achievement(
            id = "meditation_intermediate",
            title = "Практикующий медитатор",
            description = "Проведите 60 минут в медитации",
            iconResId = R.drawable.ic_achievement_meditation_intermediate,
            type = AchievementType.MEDITATION,
            points = 30,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 60,
            unlockedAt = null
        ),
        Achievement(
            id = "meditation_advanced",
            title = "Продвинутый медитатор",
            description = "Проведите 300 минут в медитации",
            iconResId = R.drawable.ic_achievement_meditation_advanced,
            type = AchievementType.MEDITATION,
            points = 100,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 300,
            unlockedAt = null
        ),
        Achievement(
            id = "meditation_master",
            title = "Мастер медитации",
            description = "Проведите 1000 минут в медитации",
            iconResId = R.drawable.ic_achievement_meditation_master,
            type = AchievementType.MEDITATION,
            points = 250,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 1000,
            unlockedAt = null
        ),
        
        // Достижения за дыхательные практики
        Achievement(
            id = "breathing_beginner",
            title = "Первый вдох",
            description = "Проведите 10 минут в дыхательных практиках",
            iconResId = R.drawable.ic_achievement_breathing_beginner,
            type = AchievementType.BREATHING,
            points = 10,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 10,
            unlockedAt = null
        ),
        Achievement(
            id = "breathing_intermediate",
            title = "Дыхательный контроль",
            description = "Проведите 60 минут в дыхательных практиках",
            iconResId = R.drawable.ic_achievement_breathing_intermediate,
            type = AchievementType.BREATHING,
            points = 30,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 60,
            unlockedAt = null
        ),
        Achievement(
            id = "breathing_advanced",
            title = "Мастер дыхания",
            description = "Проведите 300 минут в дыхательных практиках",
            iconResId = R.drawable.ic_achievement_breathing_advanced,
            type = AchievementType.BREATHING,
            points = 100,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 300,
            unlockedAt = null
        ),
        
        // Достижения за серии последовательных дней
        Achievement(
            id = "streak_week",
            title = "Первая неделя",
            description = "Поддерживайте практику 7 дней подряд",
            iconResId = R.drawable.ic_achievement_streak_week,
            type = AchievementType.STREAK,
            points = 50,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 7,
            unlockedAt = null
        ),
        Achievement(
            id = "streak_month",
            title = "Месяц постоянства",
            description = "Поддерживайте практику 30 дней подряд",
            iconResId = R.drawable.ic_achievement_streak_month,
            type = AchievementType.STREAK,
            points = 150,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 30,
            unlockedAt = null
        ),
        Achievement(
            id = "streak_season",
            title = "Сезон осознанности",
            description = "Поддерживайте практику 90 дней подряд",
            iconResId = R.drawable.ic_achievement_streak_season,
            type = AchievementType.STREAK,
            points = 300,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 90,
            unlockedAt = null
        ),
        
        // Общие достижения
        Achievement(
            id = "sessions_5",
            title = "Первые шаги",
            description = "Завершите 5 сессий",
            iconResId = R.drawable.ic_achievement_sessions_5,
            type = AchievementType.GENERAL,
            points = 10,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 5,
            unlockedAt = null
        ),
        Achievement(
            id = "sessions_25",
            title = "Регулярная практика",
            description = "Завершите 25 сессий",
            iconResId = R.drawable.ic_achievement_sessions_25,
            type = AchievementType.GENERAL,
            points = 50,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 25,
            unlockedAt = null
        ),
        Achievement(
            id = "sessions_100",
            title = "Путь осознанности",
            description = "Завершите 100 сессий",
            iconResId = R.drawable.ic_achievement_sessions_100,
            type = AchievementType.GENERAL,
            points = 150,
            isUnlocked = false,
            progress = 0f,
            requiredValue = 100,
            unlockedAt = null
        )
    )
    
    /**
     * Рассчитывает необходимое количество очков для указанного уровня
     * @param level Уровень пользователя
     * @return Необходимое количество очков
     */
    fun getPointsForLevel(level: Int): Int {
        return when {
            level <= 1 -> 0
            level <= 5 -> (level - 1) * 100
            level <= 10 -> 400 + (level - 5) * 150
            level <= 20 -> 1150 + (level - 10) * 200
            level <= 30 -> 3150 + (level - 20) * 300
            else -> 6150 + (level - 30) * 400
        }
    }
    
    /**
     * Рассчитывает уровень на основе очков
     * @param points Количество очков
     * @return Уровень пользователя
     */
    fun getLevelForPoints(points: Int): Int {
        return when {
            points < 100 -> 1
            points < 200 -> 2
            points < 300 -> 3
            points < 400 -> 4
            points < 550 -> 5
            points < 700 -> 6
            points < 850 -> 7
            points < 1000 -> 8
            points < 1150 -> 9
            points < 1350 -> 10
            points < 1550 -> 11
            points < 1750 -> 12
            points < 1950 -> 13
            points < 2150 -> 14
            points < 2350 -> 15
            points < 2550 -> 16
            points < 2750 -> 17
            points < 2950 -> 18
            points < 3150 -> 19
            points < 3450 -> 20
            points < 3750 -> 21
            points < 4050 -> 22
            points < 4350 -> 23
            points < 4650 -> 24
            points < 4950 -> 25
            points < 5250 -> 26
            points < 5550 -> 27
            points < 5850 -> 28
            points < 6150 -> 29
            points < 6550 -> 30
            else -> 30 + (points - 6150) / 400 + 1
        }
    }
} 