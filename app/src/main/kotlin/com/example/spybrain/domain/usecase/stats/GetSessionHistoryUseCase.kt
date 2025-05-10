package com.example.spybrain.domain.usecase.stats

import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionHistoryUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    operator fun invoke(): Flow<List<Session>> = statsRepository.getSessionHistory()
} 