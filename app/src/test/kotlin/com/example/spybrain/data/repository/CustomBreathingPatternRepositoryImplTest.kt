package com.example.spybrain.data.repository

import com.example.spybrain.data.model.CustomBreathingPatternEntity
import com.example.spybrain.data.storage.dao.CustomBreathingPatternDao
import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.data.model.toEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.just
import io.mockk.Runs
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
    fun `getCustomPatterns should map entities to domain models`() = runBlocking {
        val entities = listOf(
            CustomBreathingPatternEntity(
                id = "id1",
                name = "Name1",
                description = "Desc1",
                inhaleSeconds = 1,
                holdAfterInhaleSeconds = 2,
                exhaleSeconds = 3,
                holdAfterExhaleSeconds = 4,
                totalCycles = 5
            )
        )
        coEvery { dao.getAllPatterns() } returns flowOf(entities)

        val result = repository.getCustomPatterns().first()
        assertEquals(1, result.size)
        val domain = result[0]
        assertEquals("id1", domain.id)
        assertEquals("Name1", domain.name)
        assertEquals("Desc1", domain.description)
    }

    @Test
    fun `addCustomPattern should call dao insert`() = runBlocking {
        val pattern = CustomBreathingPattern(
            name = "Name",
            description = "Desc",
            inhaleSeconds = 1,
            holdAfterInhaleSeconds = 1,
            exhaleSeconds = 1,
            holdAfterExhaleSeconds = 1,
            totalCycles = 1
        )
        coEvery { dao.insertPattern(any()) } just Runs

        repository.addCustomPattern(pattern)

        coVerify(exactly = 1) { dao.insertPattern(pattern.toEntity()) }
    }

    @Test
    fun `deleteCustomPattern should call dao delete`() = runBlocking {
        val pattern = CustomBreathingPattern(
            name = "Name",
            description = "Desc",
            inhaleSeconds = 1,
            holdAfterInhaleSeconds = 1,
            exhaleSeconds = 1,
            holdAfterExhaleSeconds = 1,
            totalCycles = 1
        )
        coEvery { dao.deletePattern(any()) } just Runs

        repository.deleteCustomPattern(pattern)

        coVerify(exactly = 1) { dao.deletePattern(pattern.toEntity()) }
    }
}

