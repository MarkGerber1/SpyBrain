package com.example.spybrain.presentation.breathing

import app.cash.turbine.test
import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.domain.usecase.breathing.AddCustomBreathingPatternUseCase
import com.example.spybrain.domain.usecase.breathing.DeleteCustomBreathingPatternUseCase
import com.example.spybrain.domain.usecase.breathing.GetCustomBreathingPatternsUseCase
import com.example.spybrain.presentation.breathing.patternbuilder.BreathingPatternBuilderContract as Contract
import com.example.spybrain.presentation.breathing.patternbuilder.BreathingPatternBuilderViewModel
import com.example.spybrain.test.utils.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BreathingPatternBuilderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getUseCase = mockk<GetCustomBreathingPatternsUseCase>()
    private val addUseCase = mockk<AddCustomBreathingPatternUseCase>(relaxed = true)
    private val deleteUseCase = mockk<DeleteCustomBreathingPatternUseCase>(relaxed = true)

    private lateinit var viewModel: BreathingPatternBuilderViewModel

    @Before
    fun setUp() = runTest {
        every { getUseCase() } returns flowOf(emptyList())
        viewModel = BreathingPatternBuilderViewModel(getUseCase, addUseCase, deleteUseCase)
    }

    @Test
    fun `on name changed updates uiState`() = runTest {
        viewModel.uiState.test {
            // initial state
            val initial = awaitItem()
            assertEquals("", initial.name)
            // send event
            viewModel.setEvent(Contract.Event.EnterName("NewName"))
            val updated = awaitItem()
            assertEquals("NewName", updated.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `on save with empty name emits ShowError effect`() = runTest {
        viewModel.effect.test {
            viewModel.setEvent(Contract.Event.SavePattern)
            val effect = awaitItem() as Contract.Effect.ShowError
            assertEquals("Введите название шаблона", effect.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `on valid save calls useCase and resets state`() = runTest {
        viewModel.uiState.test {
            awaitItem() // initial
            // enter valid inputs
            viewModel.setEvent(Contract.Event.EnterName("N"))
            viewModel.setEvent(Contract.Event.EnterDescription("D"))
            viewModel.setEvent(Contract.Event.EnterInhale("1"))
            viewModel.setEvent(Contract.Event.EnterHoldInhale("2"))
            viewModel.setEvent(Contract.Event.EnterExhale("3"))
            viewModel.setEvent(Contract.Event.EnterHoldExhale("4"))
            viewModel.setEvent(Contract.Event.EnterCycles("5"))
            // consume intermediate states
            repeat(7) { awaitItem() }
            // save
            viewModel.setEvent(Contract.Event.SavePattern)
            val afterSave = awaitItem()
            // expect reset
            assertEquals("", afterSave.name)
            assertEquals("", afterSave.inhaleSeconds)
            assertEquals("", afterSave.totalCycles)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { addUseCase(any()) }
    }
} 