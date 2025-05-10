package com.example.spybrain.domain.usecase.meditation

import com.example.spybrain.domain.model.MeditationProgram
import com.example.spybrain.domain.repository.MeditationProgramRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения списка тематических медитационных программ.
 */
class GetMeditationProgramsUseCase @Inject constructor(
    private val repository: MeditationProgramRepository
) {
    operator fun invoke(): Flow<List<MeditationProgram>> = repository.getMeditationPrograms()
} 