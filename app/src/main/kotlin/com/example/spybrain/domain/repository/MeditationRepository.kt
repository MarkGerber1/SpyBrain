package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface MeditationRepository {
    fun getMeditations(): Flow<List<Meditation>>
    fun getMeditationById(id: String): Flow<Meditation?>
    suspend fun trackMeditationSession(session: Session)
} 