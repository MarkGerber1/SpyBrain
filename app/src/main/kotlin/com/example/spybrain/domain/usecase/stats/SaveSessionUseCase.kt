package com.example.spybrain.domain.usecase.stats

import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.repository.StatsRepository
import javax.inject.Inject

class SaveSessionUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    suspend operator fun invoke(session: Session) {
        statsRepository.saveSession(session)
    }
} 