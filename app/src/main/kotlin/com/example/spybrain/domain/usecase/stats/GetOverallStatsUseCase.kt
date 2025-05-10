package com.example.spybrain.domain.usecase.stats

import com.example.spybrain.domain.model.Stats
import com.example.spybrain.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOverallStatsUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    operator fun invoke(): Flow<Stats> = statsRepository.getOverallStats()
} 