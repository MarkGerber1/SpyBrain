package com.example.spybrain.domain.usecase.meditation

import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.repository.MeditationRepository
import javax.inject.Inject

class TrackMeditationSessionUseCase @Inject constructor(
    private val meditationRepository: MeditationRepository
) {
    suspend operator fun invoke(session: Session) {
        // Add validation or extra logic if needed
        meditationRepository.trackMeditationSession(session)
    }
} 