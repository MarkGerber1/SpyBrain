package com.example.spybrain.domain.usecase.achievements

import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.domain.repository.AchievementsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения списка достижений
 */
class GetAchievementsUseCase @Inject constructor(
    private val repository: AchievementsRepository
) {
    operator fun invoke(): Flow<List<Achievement>> = repository.getAchievements()
} 