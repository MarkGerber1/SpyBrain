package com.example.spybrain.domain.usecase.breathing

import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.domain.repository.CustomBreathingPatternRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCustomBreathingPatternsUseCaseTest {

    private val repository: CustomBreathingPatternRepository = mockk()
    private lateinit var useCase: GetCustomBreathingPatternsUseCase

    @Before
    fun setUp() {
        useCase = GetCustomBreathingPatternsUseCase(repository)
    }

    @Test
    fun `invoke should return patterns from repository`() = runBlocking {
        val patterns = listOf(
            CustomBreathingPattern(
                name = "Test1", description = null,
                inhaleSeconds = 1, holdAfterInhaleSeconds = 1,
                exhaleSeconds = 1, holdAfterExhaleSeconds = 1,
                totalCycles = 1
            )
        )
        every { repository.getCustomPatterns() } returns flowOf(patterns)

        val result = useCase.invoke().first()
        assertEquals(patterns, result)
    }
}

