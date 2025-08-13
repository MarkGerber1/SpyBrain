package com.example.spybrain.presentation.reminders

import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

import android.content.Context
import com.example.spybrain.data.repository.HeartRateRepository

@OptIn(ExperimentalCoroutinesApi::class)
class HeartRateViewModelTest {

    @MockK
    private lateinit var heartRateRepository: HeartRateRepository

    @MockK
    private lateinit var context: Context

    private lateinit var viewModel: HeartRateViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = HeartRateViewModel(context, heartRateRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `StartMeasurement event should start heart rate monitoring`() = runTest {
        viewModel.handleEvent(HeartRateContract.Event.StartMeasurement)
        // Состояние меняется асинхронно, дождёмся микротика
        testDispatcher.scheduler.advanceTimeBy(10)
        assertEquals(true, viewModel.uiState.value.isMeasuring)
    }

    @Test
    fun `StopMeasurement event should stop heart rate monitoring`() = runTest {
        viewModel.handleEvent(HeartRateContract.Event.StartMeasurement)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleEvent(HeartRateContract.Event.StopMeasurement)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isMeasuring)
    }

    @Test
    fun `Reset state via events sequence`() = runTest {
        viewModel.handleEvent(HeartRateContract.Event.StartMeasurement)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleEvent(HeartRateContract.Event.StopMeasurement)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isMeasuring)
    }
}

