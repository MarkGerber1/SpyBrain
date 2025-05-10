package com.example.spybrain.domain.usecase.breathing

import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.domain.repository.CustomBreathingPatternRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCustomBreathingPatternsUseCase @Inject constructor(
    private val repository: CustomBreathingPatternRepository
) {
    operator fun invoke(): Flow<List<CustomBreathingPattern>> = repository.getCustomPatterns()
} 