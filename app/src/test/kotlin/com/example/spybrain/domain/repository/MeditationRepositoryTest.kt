package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.domain.model.Session
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MeditationRepositoryTest {
    private val repository = object : MeditationRepository {
        override fun getMeditations() = flowOf(listOf(
            Meditation(
                id = "1",
                title = "Test",
                description = "Desc",
                durationMinutes = 5,
                audioUrl = "audio/test.mp3",
                category = "test"
            )
        ))

        override fun getMeditationById(id: String) = flowOf(
            Meditation(
                id = id,
                title = "Test",
                description = "Desc",
                durationMinutes = 5,
                audioUrl = "audio/test.mp3",
                category = "test"
            )
        )

        override suspend fun trackMeditationSession(session: Session) {
            // Mock implementation
        }
    }

    @Test
    fun `should return meditations`() = runTest {
        val result = repository.getMeditations()
        result.collect { list ->
            assertEquals(1, list.size)
            assertEquals("1", list[0].id)
        }
    }
}

