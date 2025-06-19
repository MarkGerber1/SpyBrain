package com.example.spybrain.domain.usecase.achievements

import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.domain.model.Stats
import javax.inject.Inject

class CheckAchievementsUseCase @Inject constructor() {
    
    operator fun invoke(stats: Stats): List<Achievement> {
        val achievements = mutableListOf<Achievement>()
        
        val totalSessions = stats.completedMeditationSessions + stats.completedBreathingSessions
        
        // Достижения за количество сессий
        when {
            totalSessions >= 1 -> achievements.add(
                Achievement(
                    id = "first_session",
                    title = "Первый шаг",
                    description = "Завершите свою первую сессию",
                    iconResId = 0,
                    points = 10
                )
            )
            totalSessions >= 5 -> achievements.add(
                Achievement(
                    id = "beginner",
                    title = "Начинающий",
                    description = "Завершите 5 сессий",
                    iconResId = 0,
                    points = 25
                )
            )
            totalSessions >= 10 -> achievements.add(
                Achievement(
                    id = "dedicated",
                    title = "Преданный",
                    description = "Завершите 10 сессий",
                    iconResId = 0,
                    points = 50
                )
            )
            totalSessions >= 25 -> achievements.add(
                Achievement(
                    id = "experienced",
                    title = "Опытный",
                    description = "Завершите 25 сессий",
                    iconResId = 0,
                    points = 100
                )
            )
            totalSessions >= 50 -> achievements.add(
                Achievement(
                    id = "master",
                    title = "Мастер",
                    description = "Завершите 50 сессий",
                    iconResId = 0,
                    points = 200
                )
            )
            totalSessions >= 100 -> achievements.add(
                Achievement(
                    id = "legend",
                    title = "Легенда",
                    description = "Завершите 100 сессий",
                    iconResId = 0,
                    points = 500
                )
            )
        }
        
        // Достижения за время медитации
        val totalMinutes = (stats.totalMeditationTimeSeconds + stats.totalBreathingTimeSeconds) / 60
        when {
            totalMinutes >= 60 -> achievements.add(
                Achievement(
                    id = "hour_meditation",
                    title = "Час спокойствия",
                    description = "Медитируйте в общей сложности 1 час",
                    iconResId = 0,
                    points = 30
                )
            )
            totalMinutes >= 300 -> achievements.add(
                Achievement(
                    id = "five_hours",
                    title = "5 часов осознанности",
                    description = "Медитируйте в общей сложности 5 часов",
                    iconResId = 0,
                    points = 150
                )
            )
            totalMinutes >= 600 -> achievements.add(
                Achievement(
                    id = "ten_hours",
                    title = "10 часов мудрости",
                    description = "Медитируйте в общей сложности 10 часов",
                    iconResId = 0,
                    points = 300
                )
            )
        }
        
        // Достижения за серию дней
        val currentStreak = stats.currentStreakDays
        when {
            currentStreak >= 3 -> achievements.add(
                Achievement(
                    id = "three_day_streak",
                    title = "Трехдневная серия",
                    description = "Медитируйте 3 дня подряд",
                    iconResId = 0,
                    points = 20
                )
            )
            currentStreak >= 7 -> achievements.add(
                Achievement(
                    id = "week_streak",
                    title = "Недельная серия",
                    description = "Медитируйте 7 дней подряд",
                    iconResId = 0,
                    points = 50
                )
            )
            currentStreak >= 30 -> achievements.add(
                Achievement(
                    id = "month_streak",
                    title = "Месячная серия",
                    description = "Медитируйте 30 дней подряд",
                    iconResId = 0,
                    points = 200
                )
            )
        }
        
        return achievements
    }
} 