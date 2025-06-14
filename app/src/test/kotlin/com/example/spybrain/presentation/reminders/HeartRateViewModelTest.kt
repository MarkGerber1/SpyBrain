package com.example.spybrain.presentation.reminders

import android.content.Context
import com.example.spybrain.data.repository.HeartRateRepository
import com.example.spybrain.presentation.reminders.HeartRateContract
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HeartRateViewModelTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var heartRateRepository: HeartRateRepository

    private lateinit var viewModel: HeartRateViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        
        coEvery { heartRateRepository.getMeasurementHistory() } returns emptyList()
        coEvery { heartRateRepository.getMotivationalPoints() } returns 0
        
        viewModel = HeartRateViewModel(context, heartRateRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        val initialState = viewModel.uiState.value
        
        assertEquals(0, initialState.currentHeartRate)
        assertFalse(initialState.isMeasuring)
        assertTrue(initialState.measurementHistory.isEmpty())
        assertEquals(0, initialState.motivationalPoints)
        assertFalse(initialState.showNewExerciseUnlocked)
    }

    @Test
    fun `startMeasurement should set isMeasuring to true`() = runTest {
        viewModel.setEvent(HeartRateContract.Event.StartMeasurement)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value.isMeasuring)
    }

    @Test
    fun `stopMeasurement should set isMeasuring to false`() = runTest {
        // Сначала запускаем измерение
        viewModel.setEvent(HeartRateContract.Event.StartMeasurement)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Затем останавливаем
        viewModel.setEvent(HeartRateContract.Event.StopMeasurement)
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertFalse(viewModel.uiState.value.isMeasuring)
    }

    @Test
    fun `measurementCompleted should save measurement and add motivational point`() = runTest {
        val testHeartRate = 75
        
        coEvery { heartRateRepository.saveMeasurement(testHeartRate) } just Runs
        coEvery { heartRateRepository.addMotivationalPoint() } returns 1
        coEvery { heartRateRepository.getMeasurementHistory() } returns listOf(testHeartRate)
        
        viewModel.setEvent(HeartRateContract.Event.MeasurementCompleted(testHeartRate))
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        verify { heartRateRepository.saveMeasurement(testHeartRate) }
        verify { heartRateRepository.addMotivationalPoint() }
        assertEquals(testHeartRate, viewModel.uiState.value.currentHeartRate)
        assertEquals(1, viewModel.uiState.value.motivationalPoints)
        assertFalse(viewModel.uiState.value.isMeasuring)
    }

    @Test
    fun `addMotivationalPoint should increment points and show unlock notification at threshold`() = runTest {
        coEvery { heartRateRepository.addMotivationalPoint() } returns 10
        
        viewModel.setEvent(HeartRateContract.Event.AddMotivationalPoint)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(10, viewModel.uiState.value.motivationalPoints)
        assertTrue(viewModel.uiState.value.showNewExerciseUnlocked)
    }

    @Test
    fun `error event should set error state`() = runTest {
        val errorMessage = "Test error message"
        
        viewModel.setEvent(HeartRateContract.Event.Error(errorMessage))
        
        assertEquals(errorMessage, viewModel.uiState.value.error)
    }
} 