package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Stats
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class StatsRepositoryTest {
    private val repository = object : StatsRepository {
        override fun getStats() = flowOf(
            Stats(
                totalSessions = 10,
                totalMinutes = 100,
                streak = 5
            )
        )
    }

    @Test
    fun `should return stats`() = runTest {
        val result = repository.getStats()
        result.collect { stats ->
            assertEquals(10, stats.totalSessions)
            assertEquals(100, stats.totalMinutes)
            assertEquals(5, stats.streak)
        }
    }
} 