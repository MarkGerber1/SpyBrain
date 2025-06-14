package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Meditation
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