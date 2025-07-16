package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Stats
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.BreathingSession
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class StatsRepositoryTest {
    private val repository = object : StatsRepository {
        override fun getOverallStats() = flowOf(
            Stats(
                totalMeditationTimeSeconds = 6000L,
                totalBreathingTimeSeconds = 3000L,
                completedMeditationSessions = 10,
                completedBreathingSessions = 5,
                currentStreakDays = 5,
                longestStreakDays = 10
            )
        )

        override fun getSessionHistory() = flowOf<List<Session>>(emptyList())

        override fun getBreathingHistory() = flowOf<List<BreathingSession>>(emptyList())

        override suspend fun saveSession(session: Session) {
            // Mock implementation
        }

        override suspend fun saveBreathingSession(session: BreathingSession) {
            // Mock implementation
        }
    }

    @Test
    fun `should return stats`() = runTest {
        val result = repository.getOverallStats()
        result.collect { stats ->
            assertEquals(6000L, stats.totalMeditationTimeSeconds)
            assertEquals(3000L, stats.totalBreathingTimeSeconds)
            assertEquals(10, stats.completedMeditationSessions)
            assertEquals(5, stats.completedBreathingSessions)
            assertEquals(5, stats.currentStreakDays)
            assertEquals(10, stats.longestStreakDays)
        }
    }
}

