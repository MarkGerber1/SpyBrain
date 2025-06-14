package com.example.spybrain.domain.usecase.meditation

import app.cash.turbine.test
import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.domain.repository.MeditationRepository
import com.example.spybrain.test.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetMeditationsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var meditationRepository: MeditationRepository
    private lateinit var getMeditationsUseCase: GetMeditationsUseCase

    @Before
    fun setup() {
        meditationRepository = mockk()
        getMeditationsUseCase = GetMeditationsUseCase(meditationRepository)
    }

    @Test
    fun `should return empty list when repository returns empty`() = runTest {
        // Given
        coEvery { meditationRepository.getMeditations() } returns flowOf(emptyList())

        // When & Then
        getMeditationsUseCase().test {
            val meditations = awaitItem()
            assertTrue(meditations.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `should return meditations from repository`() = runTest {
        // Given
        val expectedMeditations = listOf(
            Meditation(
                id = "1",
                title = "Медитация для сна",
                description = "Расслабляющая медитация",
                durationMinutes = 10,
                audioUrl = "audio/sleep.mp3",
                category = "sleep"
            ),
            Meditation(
                id = "2",
                title = "Утренняя медитация",
                description = "Энергичная медитация",
                durationMinutes = 5,
                audioUrl = "audio/morning.mp3",
                category = "morning"
            )
        )
        coEvery { meditationRepository.getMeditations() } returns flowOf(expectedMeditations)

        // When & Then
        getMeditationsUseCase().test {
            val meditations = awaitItem()
            assertEquals(expectedMeditations.size, meditations.size)
            assertEquals(expectedMeditations[0].id, meditations[0].id)
            assertEquals(expectedMeditations[0].title, meditations[0].title)
            assertEquals(expectedMeditations[1].id, meditations[1].id)
            assertEquals(expectedMeditations[1].title, meditations[1].title)
            awaitComplete()
        }
    }

    @Test
    fun `should propagate error from repository`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { meditationRepository.getMeditations() } throws exception

        // When & Then
        getMeditationsUseCase().test {
            val error = awaitError()
            assertEquals(exception, error)
        }
    }
} 