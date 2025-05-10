package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.Stats
import com.example.spybrain.domain.model.BreathingSession
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    fun getOverallStats(): Flow<Stats>
    fun getSessionHistory(): Flow<List<Session>>
    fun getBreathingHistory(): Flow<List<BreathingSession>>
    suspend fun saveSession(session: Session)
    suspend fun saveBreathingSession(session: BreathingSession)
} 