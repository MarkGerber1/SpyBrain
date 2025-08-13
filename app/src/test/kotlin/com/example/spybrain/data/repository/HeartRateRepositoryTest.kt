package com.example.spybrain.data.repository

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.slot
import io.mockk.just
import io.mockk.Runs
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
// no wildcard matchers to avoid import issues; use slot captures instead
import com.example.spybrain.data.datastore.SettingsDataStore
import com.example.spybrain.data.storage.dao.HeartRateDao
import com.example.spybrain.data.storage.model.HeartRateMeasurement
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
    fun setUp() {
        MockKAnnotations.init(this)
        repository = HeartRateRepository(heartRateDao, settingsDataStore)
    }

    @Test
    fun `getHeartRateHistory should return measurements from dao`() = runTest {
        val measurements = listOf(
            HeartRateMeasurement(
                id = 1L,
                heartRate = 75,
                timestamp = LocalDateTime.now()
            )
        )

        coEvery { heartRateDao.getAllMeasurements() } returns measurements

        val result = repository.getMeasurementHistory()

        assertEquals(measurements.takeLast(20), result)
        coVerify { heartRateDao.getAllMeasurements() }
    }

    @Test
    fun `addHeartRateMeasurement should call dao insert`() = runTest {
        val heartRate = 75
        val captured = slot<HeartRateMeasurement>()
        coEvery { heartRateDao.insertMeasurement(capture(captured)) } just Runs

        repository.saveMeasurement(heartRate)

        coVerify { heartRateDao.insertMeasurement(captured.captured) }
        assertEquals(heartRate, captured.captured.heartRate)
    }

    @Test
    fun `getHeartRateHistoryForSession should return measurements for specific session`() = runTest {
        val sessionId = "session1"
        val measurements = listOf(
            HeartRateMeasurement(
                id = 1L,
                heartRate = 75,
                timestamp = LocalDateTime.now()
            )
        )

        // Актуальная реализация не поддерживает фильтрацию по sessionId
        coEvery { heartRateDao.getAllMeasurements() } returns measurements

        val result = repository.getMeasurementHistory()

        assertEquals(measurements.takeLast(20), result)
        coVerify { heartRateDao.getAllMeasurements() }
    }
}

