package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.CustomBreathingPattern
import kotlinx.coroutines.flow.Flow

/** Репозиторий для работы с пользовательскими шаблонами дыхания */
interface CustomBreathingPatternRepository {
    /** Получение всех пользовательских шаблонов */
    fun getCustomPatterns(): Flow<List<CustomBreathingPattern>>

    /** Добавление или обновление шаблона */
    suspend fun addCustomPattern(pattern: CustomBreathingPattern)

    /** Удаление шаблона */
    suspend fun deleteCustomPattern(pattern: CustomBreathingPattern)
} 