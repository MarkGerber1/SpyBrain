package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.MeditationProgram
import kotlinx.coroutines.flow.Flow

/** Репозиторий для тематических медитационных программ */
interface MeditationProgramRepository {
    /** Получение списка программ */
    fun getMeditationPrograms(): Flow<List<MeditationProgram>>
} 