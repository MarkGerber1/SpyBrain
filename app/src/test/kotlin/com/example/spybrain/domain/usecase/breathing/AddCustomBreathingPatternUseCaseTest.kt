package com.example.spybrain.domain.usecase.breathing

import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.domain.repository.CustomBreathingPatternRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.just
import io.mockk.Runs
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AddCustomBreathingPatternUseCaseTest {

    private val repository: CustomBreathingPatternRepository = mockk()
    private lateinit var useCase: AddCustomBreathingPatternUseCase

    @Before
    fun setUp() {
        useCase = AddCustomBreathingPatternUseCase(repository)
    }

    @Test
    fun `invoke should call addCustomPattern on repository`() = runBlocking {
        val pattern = CustomBreathingPattern(
            name = "Test",
            description = "Desc",
            inhaleSeconds = 3,
            holdAfterInhaleSeconds = 1,
            exhaleSeconds = 4,
            holdAfterExhaleSeconds = 1,
            totalCycles = 5
        )
        coEvery { repository.addCustomPattern(pattern) } just Runs

        useCase.invoke(pattern)

        coVerify(exactly = 1) { repository.addCustomPattern(pattern) }
    }
}

