package com.example.spybrain.domain.usecase.achievements

import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.domain.model.Stats
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РїСЂРѕРІРµСЂРєРё РґРѕСЃС‚РёР¶РµРЅРёР№ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
class CheckAchievementsUseCase @Inject constructor() {

    /**
     * РџСЂРѕРІРµСЂСЏРµС‚ РґРѕСЃС‚РёР¶РµРЅРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @param userId РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РЎРїРёСЃРѕРє РЅРѕРІС‹С… РґРѕСЃС‚РёР¶РµРЅРёР№.
     */
    operator fun invoke(stats: Stats): List<Achievement> {
        val achievements = mutableListOf<Achievement>()

        val totalSessions = stats.completedMeditationSessions + stats.completedBreathingSessions

        // Р”РѕСЃС‚РёР¶РµРЅРёСЏ Р·Р° РєРѕР»РёС‡РµСЃС‚РІРѕ СЃРµСЃСЃРёР№
        when {
            totalSessions >= 1 -> achievements.add(
                Achievement(
                    id = "first_session",
                    title = "РџРµСЂРІС‹Р№ С€Р°Рі",
                    description = "Р—Р°РІРµСЂС€РёС‚Рµ СЃРІРѕСЋ РїРµСЂРІСѓСЋ СЃРµСЃСЃРёСЋ",
                    iconResId = 0,
                    points = 10
                )
            )
            totalSessions >= 5 -> achievements.add(
                Achievement(
                    id = "beginner",
                    title = "РќР°С‡РёРЅР°СЋС‰РёР№",
                    description = "Р—Р°РІРµСЂС€РёС‚Рµ 5 СЃРµСЃСЃРёР№",
                    iconResId = 0,
                    points = 25
                )
            )
            totalSessions >= 10 -> achievements.add(
                Achievement(
                    id = "dedicated",
                    title = "РџСЂРµРґР°РЅРЅС‹Р№",
                    description = "Р—Р°РІРµСЂС€РёС‚Рµ 10 СЃРµСЃСЃРёР№",
                    iconResId = 0,
                    points = 50
                )
            )
            totalSessions >= 25 -> achievements.add(
                Achievement(
                    id = "experienced",
                    title = "РћРїС‹С‚РЅС‹Р№",
                    description = "Р—Р°РІРµСЂС€РёС‚Рµ 25 СЃРµСЃСЃРёР№",
                    iconResId = 0,
                    points = 100
                )
            )
            totalSessions >= 50 -> achievements.add(
                Achievement(
                    id = "master",
                    title = "РњР°СЃС‚РµСЂ",
                    description = "Р—Р°РІРµСЂС€РёС‚Рµ 50 СЃРµСЃСЃРёР№",
                    iconResId = 0,
                    points = 200
                )
            )
            totalSessions >= 100 -> achievements.add(
                Achievement(
                    id = "legend",
                    title = "Р›РµРіРµРЅРґР°",
                    description = "Р—Р°РІРµСЂС€РёС‚Рµ 100 СЃРµСЃСЃРёР№",
                    iconResId = 0,
                    points = 500
                )
            )
        }

        // Р”РѕСЃС‚РёР¶РµРЅРёСЏ Р·Р° РІСЂРµРјСЏ РјРµРґРёС‚Р°С†РёРё
        val totalMinutes = (stats.totalMeditationTimeSeconds + stats.totalBreathingTimeSeconds) / 60
        when {
            totalMinutes >= 60 -> achievements.add(
                Achievement(
                    id = "hour_meditation",
                    title = "Р§Р°СЃ СЃРїРѕРєРѕР№СЃС‚РІРёСЏ",
                    description = "РњРµРґРёС‚РёСЂСѓР№С‚Рµ РІ РѕР±С‰РµР№ СЃР»РѕР¶РЅРѕСЃС‚Рё 1 С‡Р°СЃ",
                    iconResId = 0,
                    points = 30
                )
            )
            totalMinutes >= 300 -> achievements.add(
                Achievement(
                    id = "five_hours",
                    title = "5 С‡Р°СЃРѕРІ РѕСЃРѕР·РЅР°РЅРЅРѕСЃС‚Рё",
                    description = "РњРµРґРёС‚РёСЂСѓР№С‚Рµ РІ РѕР±С‰РµР№ СЃР»РѕР¶РЅРѕСЃС‚Рё 5 С‡Р°СЃРѕРІ",
                    iconResId = 0,
                    points = 150
                )
            )
            totalMinutes >= 600 -> achievements.add(
                Achievement(
                    id = "ten_hours",
                    title = "10 С‡Р°СЃРѕРІ РјСѓРґСЂРѕСЃС‚Рё",
                    description = "РњРµРґРёС‚РёСЂСѓР№С‚Рµ РІ РѕР±С‰РµР№ СЃР»РѕР¶РЅРѕСЃС‚Рё 10 С‡Р°СЃРѕРІ",
                    iconResId = 0,
                    points = 300
                )
            )
        }

        // Р”РѕСЃС‚РёР¶РµРЅРёСЏ Р·Р° СЃРµСЂРёСЋ РґРЅРµР№
        val currentStreak = stats.currentStreakDays
        when {
            currentStreak >= 3 -> achievements.add(
                Achievement(
                    id = "three_day_streak",
                    title = "РўСЂРµС…РґРЅРµРІРЅР°СЏ СЃРµСЂРёСЏ",
                    description = "РњРµРґРёС‚РёСЂСѓР№С‚Рµ 3 РґРЅСЏ РїРѕРґСЂСЏРґ",
                    iconResId = 0,
                    points = 20
                )
            )
            currentStreak >= 7 -> achievements.add(
                Achievement(
                    id = "week_streak",
                    title = "РќРµРґРµР»СЊРЅР°СЏ СЃРµСЂРёСЏ",
                    description = "РњРµРґРёС‚РёСЂСѓР№С‚Рµ 7 РґРЅРµР№ РїРѕРґСЂСЏРґ",
                    iconResId = 0,
                    points = 50
                )
            )
            currentStreak >= 30 -> achievements.add(
                Achievement(
                    id = "month_streak",
                    title = "РњРµСЃСЏС‡РЅР°СЏ СЃРµСЂРёСЏ",
                    description = "РњРµРґРёС‚РёСЂСѓР№С‚Рµ 30 РґРЅРµР№ РїРѕРґСЂСЏРґ",
                    iconResId = 0,
                    points = 200
                )
            )
        }

        return achievements
    }
}
