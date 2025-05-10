package com.example.spybrain.domain.usecase.stats

import com.example.spybrain.domain.model.BreathingSession
import com.example.spybrain.domain.repository.StatsRepository
import javax.inject.Inject

class SaveBreathingSessionUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    suspend operator fun invoke(session: BreathingSession) {
        statsRepository.saveBreathingSession(session)
    }
} 