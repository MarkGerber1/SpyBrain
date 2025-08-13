package com.example.spybrain.data.repository

import com.example.spybrain.data.model.CustomBreathingPatternEntity
import com.example.spybrain.data.storage.dao.CustomBreathingPatternDao
import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.data.model.toEntity
import com.example.spybrain.data.model.toDomain
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.just
import io.mockk.Runs
// avoid wildcard any import; not required
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CustomBreathingPatternRepositoryImplTest {
    private val dao: CustomBreathingPatternDao = mockk()
    private lateinit var repository: CustomBreathingPatternRepositoryImpl

    @Before
    fun setUp() {
        repository = CustomBreathingPatternRepositoryImpl(dao)
    }

    @Test
    fun `getAll should return patterns from dao`() = runBlocking {
        val entities = listOf(
            CustomBreathingPatternEntity(
                id = "1",
                name = "Test Pattern",
                description = "Test Description",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 7,
                exhaleSeconds = 8,
                holdAfterExhaleSeconds = 0,
                totalCycles = 10
            )
        )
        val expectedPatterns = entities.map { it.toDomain() }

        coEvery { dao.getAllPatterns() } returns flowOf(entities)

        val result = repository.getAll()

        assertEquals(expectedPatterns, result)
        coVerify { dao.getAllPatterns() }
    }

    @Test
    fun `add should call dao insert`() = runBlocking {
        val pattern = CustomBreathingPattern(
            id = 1L,
            name = "Test Pattern",
            description = "Test Description",
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 7,
            exhaleSeconds = 8,
            holdAfterExhaleSeconds = 0,
            totalCycles = 10
        )

        coEvery { dao.insertPattern(any()) } just Runs

        repository.add(pattern)

        coVerify { dao.insertPattern(any()) }
    }

    @Test
    fun `delete should call dao delete`() = runBlocking {
        val patternId = 1L
        val entity = CustomBreathingPatternEntity(
            id = patternId.toString(),
            name = "Test Pattern",
            description = null,
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 7,
            exhaleSeconds = 8,
            holdAfterExhaleSeconds = 0,
            totalCycles = 10
        )

        coEvery { dao.getAllPatterns() } returns flowOf(listOf(entity))
        coEvery { dao.deletePattern(any()) } just Runs

        repository.delete(patternId)

        coVerify { dao.deletePattern(any()) }
    }
}

