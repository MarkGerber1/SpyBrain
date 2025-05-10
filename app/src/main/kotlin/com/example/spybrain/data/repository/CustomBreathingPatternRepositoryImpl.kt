package com.example.spybrain.data.repository

import com.example.spybrain.data.model.toDomain
import com.example.spybrain.data.model.toEntity
import com.example.spybrain.data.storage.dao.CustomBreathingPatternDao
import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.domain.repository.CustomBreathingPatternRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Реализация репозитория для пользовательских шаблонов дыхания */
@Singleton
class CustomBreathingPatternRepositoryImpl @Inject constructor(
    private val dao: CustomBreathingPatternDao
) : CustomBreathingPatternRepository {
    override fun getCustomPatterns(): Flow<List<CustomBreathingPattern>> =
        dao.getAllPatterns().map { list -> list.map { it.toDomain() } }

    override suspend fun addCustomPattern(pattern: CustomBreathingPattern) {
        dao.insertPattern(pattern.toEntity())
    }

    override suspend fun deleteCustomPattern(pattern: CustomBreathingPattern) {
        dao.deletePattern(pattern.toEntity())
    }
} 