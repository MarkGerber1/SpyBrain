package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.BreathingPattern
import com.example.spybrain.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface BreathingRepository {
    fun getBreathingPatterns(): Flow<List<BreathingPattern>>
    fun getBreathingPatternById(id: String): Flow<BreathingPattern?>
    suspend fun trackBreathingSession(session: Session)
} 