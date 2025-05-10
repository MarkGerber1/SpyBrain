package com.example.spybrain.domain.usecase.meditation

import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.domain.repository.MeditationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeditationsUseCase @Inject constructor(
    private val meditationRepository: MeditationRepository
) {
    operator fun invoke(): Flow<List<Meditation>> = meditationRepository.getMeditations()
} 