package com.example.spybrain.data.repository

import com.example.spybrain.data.datastore.SettingsDataStore
import com.example.spybrain.data.storage.dao.HeartRateDao
import com.example.spybrain.data.storage.model.HeartRateMeasurement
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class HeartRateRepositoryTest {

    @MockK
    private lateinit var heartRateDao: HeartRateDao

    @MockK
    private lateinit var settingsDataStore: SettingsDataStore

    private lateinit var repository: HeartRateRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = HeartRateRepository(heartRateDao, settingsDataStore)
    }

    @Test
    fun `saveMeasurement should insert measurement into dao`() = runTest {
        val heartRate = 75
        coEvery { heartRateDao.insertMeasurement(any()) } just Runs

        repository.saveMeasurement(heartRate)

        coVerify { heartRateDao.insertMeasurement(any()) }
    }

    @Test
    fun `getMeasurementHistory should return last 20 measurements`() = runTest {
        val measurements = (1..25).map { HeartRateMeasurement(it.toLong(), 60 + it, LocalDateTime.now()) }
        coEvery { heartRateDao.getAllMeasurements() } returns measurements

        val result = repository.getMeasurementHistory()

        assertEquals(20, result.size)
        assertEquals(85, result.first()) // Последнее измерение (25 + 60)
        assertEquals(61, result.last()) // Первое измерение из последних 20 (6 + 60)
    }

    @Test
    fun `getMotivationalPoints should return points from dataStore`() = runTest {
        val expectedPoints = 42
        coEvery { settingsDataStore.getMotivationalPoints() } returns expectedPoints

        val result = repository.getMotivationalPoints()

        assertEquals(expectedPoints, result)
    }

    @Test
    fun `addMotivationalPoint should increment points in dataStore`() = runTest {
        val currentPoints = 10
        val newPoints = 11
        coEvery { settingsDataStore.getMotivationalPoints() } returns currentPoints
        coEvery { settingsDataStore.setMotivationalPoints(newPoints) } just Runs

        val result = repository.addMotivationalPoint()

        assertEquals(newPoints, result)
        coVerify { settingsDataStore.setMotivationalPoints(newPoints) }
    }

    @Test
    fun `getAverageHeartRate should return average when measurements exist`() = runTest {
        val measurements = listOf(
            HeartRateMeasurement(1, 60, LocalDateTime.now()),
            HeartRateMeasurement(2, 80, LocalDateTime.now()),
            HeartRateMeasurement(3, 100, LocalDateTime.now())
        )
        coEvery { heartRateDao.getAllMeasurements() } returns measurements

        val result = repository.getAverageHeartRate()

        assertEquals(80f, result)
    }

    @Test
    fun `getAverageHeartRate should return 0 when no measurements exist`() = runTest {
        coEvery { heartRateDao.getAllMeasurements() } returns emptyList()

        val result = repository.getAverageHeartRate()

        assertEquals(0f, result)
    }

    @Test
    fun `clearHistory should delete all measurements`() = runTest {
        coEvery { heartRateDao.deleteAllMeasurements() } just Runs

        repository.clearHistory()

        coVerify { heartRateDao.deleteAllMeasurements() }
    }
} 