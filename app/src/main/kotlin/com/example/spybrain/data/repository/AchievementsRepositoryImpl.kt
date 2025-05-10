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

@Singleton
class AchievementsRepositoryImpl @Inject constructor(
    private val dao: AchievementDao
) : AchievementsRepository {

    private val defaultAchievements = listOf(
        AchievementEntity(
            id = "first_practice",
            title = "Первая практика",
            description = "Завершить первую дыхательную или медитационную сессию"
        ),
        AchievementEntity(
            id = "three_days",
            title = "3 дня подряд",
            description = "Запуск сессий в 3 разных дня без пропуска"
        ),
        AchievementEntity(
            id = "ten_breath",
            title = "10 дыхательных сессий",
            description = "Завершено ≥ 10 дыхательных практик"
        ),
        AchievementEntity(
            id = "five_meditations",
            title = "5 медитаций",
            description = "Завершено ≥ 5 медитационных сессий"
        ),
        AchievementEntity(
            id = "personalization",
            title = "Персонализация",
            description = "Включены индивидуальные настройки"
        ),
        AchievementEntity(
            id = "voice_command",
            title = "Голосовая команда",
            description = "Пользователь впервые использовал голосовую команду"
        )
    )

    override fun getAchievements(): Flow<List<Achievement>> = flow {
        // Предварительная инициализация списка достижений
        val existing = dao.getAll().first()
        if (existing.isEmpty()) {
            defaultAchievements.forEach { dao.insert(it) }
        } else {
            // Добавляем пропущенные дефолтные достижения
            defaultAchievements.filter { def -> existing.none { it.id == def.id } }
                .forEach { dao.insert(it) }
        }
        // Возвращаем поток со списком доменных моделей
        dao.getAll().map { list -> list.map { it.toDomain() } }
            .collect { emit(it) }
    }

    override suspend fun unlockAchievement(id: String, unlockedAt: Long) {
        // Обновляем достижение: помечаем как разблокированное
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