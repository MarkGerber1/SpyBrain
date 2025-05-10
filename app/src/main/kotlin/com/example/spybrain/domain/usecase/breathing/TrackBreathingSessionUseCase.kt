package com.example.spybrain.domain.usecase.breathing

import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.repository.BreathingRepository
import javax.inject.Inject

class TrackBreathingSessionUseCase @Inject constructor(
    private val breathingRepository: BreathingRepository
) {
    suspend operator fun invoke(session: Session) {
        // Add validation or extra logic if needed
        breathingRepository.trackBreathingSession(session)
    }
} 