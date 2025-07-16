package com.example.spybrain.domain.model

/**
 * РЎС‚Р°С‚РёСЃС‚РёРєР° РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 * @property totalMeditationTimeSeconds РћР±С‰РµРµ РІСЂРµРјСЏ РјРµРґРёС‚Р°С†РёРё РІ СЃРµРєСѓРЅРґР°С….
 * @property totalBreathingTimeSeconds РћР±С‰РµРµ РІСЂРµРјСЏ РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїСЂР°РєС‚РёРє РІ СЃРµРєСѓРЅРґР°С….
 * @property completedMeditationSessions РљРѕР»РёС‡РµСЃС‚РІРѕ Р·Р°РІРµСЂС€С‘РЅРЅС‹С… СЃРµСЃСЃРёР№ РјРµРґРёС‚Р°С†РёРё.
 * @property completedBreathingSessions РљРѕР»РёС‡РµСЃС‚РІРѕ Р·Р°РІРµСЂС€С‘РЅРЅС‹С… СЃРµСЃСЃРёР№ РґС‹С…Р°РЅРёСЏ.
 * @property currentStreakDays РўРµРєСѓС‰Р°СЏ СЃРµСЂРёСЏ РґРЅРµР№.
 * @property longestStreakDays РЎР°РјР°СЏ РґР»РёРЅРЅР°СЏ СЃРµСЂРёСЏ РґРЅРµР№.
 */
data class Stats(
    val totalMeditationTimeSeconds: Long,
    val totalBreathingTimeSeconds: Long,
    val completedMeditationSessions: Int,
    val completedBreathingSessions: Int,
    val currentStreakDays: Int,
    val longestStreakDays: Int
    // Add more specific stats as needed
)
