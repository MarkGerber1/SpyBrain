package com.example.spybrain.data.repository

import com.example.spybrain.data.model.AchievementEntity
import com.example.spybrain.data.model.toDomain
import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.domain.repository.AchievementsRepository
import com.example.spybrain.data.storage.dao.AchievementDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Р РµР°Р»РёР·Р°С†РёСЏ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ РґРѕСЃС‚РёР¶РµРЅРёР№. РЎРѕС…СЂР°РЅСЏРµС‚ Рё РїСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ РґРѕСЃС‚РёР¶РµРЅРёСЏ С‡РµСЂРµР· DAO.
 */
@Singleton
class AchievementsRepositoryImpl @Inject constructor(
    private val dao: AchievementDao
) : AchievementsRepository {

    private val defaultAchievements = listOf(
        AchievementEntity(
            id = "first_practice",
            title = "Первая практика",
            description = "Завершить первую дыхательную или медитационную сессию",
            isUnlocked = false,
            unlockedAt = null
        ),
        AchievementEntity(
            id = "three_days",
            title = "3 дня подряд",
            description = "Запуск сессий в 3 разных дня без пропуска",
            isUnlocked = false,
            unlockedAt = null
        ),
        AchievementEntity(
            id = "ten_breath",
            title = "10 дыхательных сессий",
            description = "Завершено ≥ 10 дыхательных практик",
            isUnlocked = false,
            unlockedAt = null
        ),
        AchievementEntity(
            id = "five_meditations",
            title = "5 медитаций",
            description = "Завершено ≥ 5 медитационных сессий",
            isUnlocked = false,
            unlockedAt = null
        ),
        AchievementEntity(
            id = "personalization",
            title = "Персонализация",
            description = "Включены индивидуальные настройки",
            isUnlocked = false,
            unlockedAt = null
        ),
        AchievementEntity(
            id = "voice_command",
            title = "Голосовая команда",
            description = "Пользователь впервые использовал голосовую команду",
            isUnlocked = false,
            unlockedAt = null
        )
    )

    override fun getAllAchievements(): List<Achievement> {
        // Dummy implementation
        return defaultAchievements.map { it.toDomain() }
    }

    override fun checkAchievements(userId: String): List<Achievement> {
        // Dummy implementation
        return defaultAchievements.map { it.toDomain() }
    }

    override fun getAchievements(): Flow<List<Achievement>> = flow {
        val existing = dao.getAll().first()
        if (existing.isEmpty()) {
            defaultAchievements.forEach { dao.insert(it) }
        } else {
            defaultAchievements.filter { def -> existing.none { it.id == def.id } }
                .forEach { dao.insert(it) }
        }
        dao.getAll().map { list -> list.map { it.toDomain() } }
            .collect { emit(it) }
    }

    override suspend fun unlockAchievement(id: String, unlockedAt: Long) {
        val def = defaultAchievements.find { it.id == id }
        val entity = AchievementEntity(
            id = id,
            title = def?.title ?: id,
            description = def?.description ?: "",
            isUnlocked = true,
            unlockedAt = unlockedAt
        )
        dao.insert(entity)
    }
}
