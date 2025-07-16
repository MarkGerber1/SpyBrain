package com.example.spybrain.data.repository

import com.example.spybrain.data.model.toDomain
import com.example.spybrain.data.model.toEntity
import com.example.spybrain.data.storage.dao.CustomBreathingPatternDao
import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.domain.repository.CustomBreathingPatternRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация репозитория пользовательских дыхательных паттернов.
 * Осуществляет доступ к DAO.
 */
@Singleton
class CustomBreathingPatternRepositoryImpl @Inject constructor(
    private val dao: CustomBreathingPatternDao
) : CustomBreathingPatternRepository {
    override fun getAll(): List<CustomBreathingPattern> {
        // For now, block and get the first value from the Flow
        return try {
            val patterns = dao.getAllPatterns()
            val list = kotlinx.coroutines.runBlocking { patterns.firstOrNull() ?: emptyList() }
            list.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun add(pattern: CustomBreathingPattern) {
        kotlinx.coroutines.runBlocking {
            dao.insertPattern(pattern.toEntity())
        }
    }

    override fun delete(id: Long) {
        kotlinx.coroutines.runBlocking {
            val patterns = dao.getAllPatterns().firstOrNull() ?: emptyList()
            val entity = patterns.find { it.id.toLongOrNull() == id }
            if (entity != null) {
                dao.deletePattern(entity)
            }
        }
    }
}
