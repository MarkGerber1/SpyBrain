package com.example.spybrain.presentation.onboarding

import com.example.spybrain.domain.repository.SettingsRepository
import com.example.spybrain.presentation.onboarding.OnboardingContract
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Тесты для OnboardingViewModel.
 * Проверяют корректность валидации данных и сохранения настроек.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private lateinit var viewModel: OnboardingViewModel
    private lateinit var settingsRepository: SettingsRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        settingsRepository = mockk(relaxed = true)

        viewModel = OnboardingViewModel(settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() {
        val initialState = viewModel.uiState.value

        assertEquals("", initialState.name)
        assertEquals(null, initialState.selectedDate)
        assertFalse(initialState.isNameValid)
        assertFalse(initialState.isDateValid)
        assertFalse(initialState.canContinue)
        assertFalse(initialState.isLoading)
    }

    @Test
    fun `nameEntered should update state and validate name`() = runTest {
        val validName = "Алексей"

        viewModel.setEvent(OnboardingContract.Event.NameEntered(validName))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(validName, state.name)
        assertTrue(state.isNameValid)
    }

    @Test
    fun `nameEntered with invalid name should not validate`() = runTest {
        val invalidName = "A" // Слишком короткое имя

        viewModel.setEvent(OnboardingContract.Event.NameEntered(invalidName))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(invalidName, state.name)
        assertFalse(state.isNameValid)
        assertFalse(state.canContinue)
    }

    @Test
    fun `dateSelected should update state and validate date`() = runTest {
        val validDate = LocalDate.now().minusYears(25)

        viewModel.setEvent(OnboardingContract.Event.DateSelected(validDate))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(validDate, state.selectedDate)
        assertTrue(state.isDateValid)
    }

    @Test
    fun `dateSelected with future date should not validate`() = runTest {
        val futureDate = LocalDate.now().plusDays(1)

        viewModel.setEvent(OnboardingContract.Event.DateSelected(futureDate))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(futureDate, state.selectedDate)
        assertFalse(state.isDateValid)
        assertFalse(state.canContinue)
    }

    @Test
    fun `continueClicked with valid data should save settings and complete onboarding`() = runTest {
        // Given
        val validName = "Алексей"
        val validDate = LocalDate.now().minusYears(25)

        viewModel.setEvent(OnboardingContract.Event.NameEntered(validName))
        viewModel.setEvent(OnboardingContract.Event.DateSelected(validDate))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.setEvent(OnboardingContract.Event.ContinueClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            settingsRepository.setUserName(validName)
            settingsRepository.setUserBirthDate(validDate)
            settingsRepository.setOnboardingCompleted(true)
        }
    }

    @Test
    fun `continueClicked with invalid data should not save and show error`() = runTest {
        // Given - только имя без даты
        val validName = "Алексей"

        viewModel.setEvent(OnboardingContract.Event.NameEntered(validName))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.setEvent(OnboardingContract.Event.ContinueClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) {
            settingsRepository.setUserName(any())
            settingsRepository.setUserBirthDate(any())
            settingsRepository.setOnboardingCompleted(any())
        }
    }

    @Test
    fun `validateData should update canContinue correctly`() = runTest {
        // Given - сначала только имя
        val validName = "Алексей"
        viewModel.setEvent(OnboardingContract.Event.NameEntered(validName))
        testDispatcher.scheduler.advanceUntilIdle()

        // Проверяем что canContinue = false без даты
        var state = viewModel.uiState.value
        assertFalse(state.canContinue)

        // When - добавляем дату
        val validDate = LocalDate.now().minusYears(25)
        viewModel.setEvent(OnboardingContract.Event.DateSelected(validDate))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        state = viewModel.uiState.value
        assertTrue(state.canContinue)
    }
}