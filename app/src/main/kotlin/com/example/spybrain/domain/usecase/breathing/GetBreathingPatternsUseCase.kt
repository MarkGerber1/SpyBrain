package com.example.spybrain.domain.usecase.breathing

import com.example.spybrain.domain.model.BreathingPattern
import com.example.spybrain.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBreathingPatternsUseCase @Inject constructor(
    private val breathingRepository: BreathingRepository
) {
    operator fun invoke(): Flow<List<BreathingPattern>> = breathingRepository.getBreathingPatterns()
} 