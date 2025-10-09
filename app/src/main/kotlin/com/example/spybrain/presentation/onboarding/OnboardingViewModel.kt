package com.example.spybrain.presentation.onboarding

import androidx.lifecycle.viewModelScope
import com.example.spybrain.domain.repository.SettingsRepository
import com.example.spybrain.presentation.base.BaseViewModel
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * Контракт для экрана онбординга.
 */
interface OnboardingContract {
    /**
     * Состояние экрана онбординга.
     */
    data class State(
        val name: String = "",
        val selectedDate: LocalDate? = null,
        val isNameValid: Boolean = false,
        val isDateValid: Boolean = false,
        val canContinue: Boolean = false,
        val isLoading: Boolean = false
    ) : UiState

    /**
     * События экрана онбординга.
     */
    sealed class Event : UiEvent {
        /** Ввод имени пользователя. */
        data class NameEntered(val name: String) : Event()

        /** Выбор даты рождения. */
        data class DateSelected(val date: LocalDate) : Event()

        /** Нажатие кнопки "Продолжить". */
        object ContinueClicked : Event()

        /** Проверка валидности данных. */
        object ValidateData : Event()
    }

    /**
     * Эффекты экрана онбординга.
     */
    sealed class Effect : UiEffect {
        /** Показать ошибку валидации. */
        data class ShowValidationError(val message: String) : Effect()

        /** Завершить онбординг и перейти к главному экрану. */
        object CompleteOnboarding : Effect()
    }
}

/**
 * ViewModel экрана онбординга.
 * Управляет вводом имени и даты рождения пользователя.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BaseViewModel<OnboardingContract.Event, OnboardingContract.State, OnboardingContract.Effect>(
    OnboardingContract.State()
) {

    override fun handleEvent(event: OnboardingContract.Event) {
        when (event) {
            is OnboardingContract.Event.NameEntered -> handleNameEntered(event.name)
            is OnboardingContract.Event.DateSelected -> handleDateSelected(event.date)
            is OnboardingContract.Event.ContinueClicked -> handleContinueClicked()
            is OnboardingContract.Event.ValidateData -> validateData()
        }
    }

    private fun handleNameEntered(name: String) {
        setState { copy(name = name) }
        validateData()
    }

    private fun handleDateSelected(date: LocalDate) {
        setState { copy(selectedDate = date) }
        validateData()
    }

    private fun handleContinueClicked() {
        val currentState = uiState.value

        if (!currentState.canContinue) {
            setEffect { OnboardingContract.Effect.ShowValidationError("Заполните все поля корректно") }
            return
        }

        setState { copy(isLoading = true) }

        launchAsync {
            try {
                // Сохраняем данные пользователя
                settingsRepository.setUserName(currentState.name)
                settingsRepository.setUserBirthDate(currentState.selectedDate!!)

                // Помечаем онбординг как завершенный
                settingsRepository.setOnboardingCompleted(true)

                setState { copy(isLoading = false) }
                setEffect { OnboardingContract.Effect.CompleteOnboarding }
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
                setEffect { OnboardingContract.Effect.ShowValidationError("Ошибка сохранения данных") }
            }
        }
    }

    private fun validateData() {
        val currentState = uiState.value

        val isNameValid = currentState.name.length >= 2 &&
                         currentState.name.matches(Regex("^[а-яА-ЯёЁa-zA-Z\\s]+$"))

        val isDateValid = currentState.selectedDate != null &&
                         currentState.selectedDate.isBefore(LocalDate.now())

        val canContinue = isNameValid && isDateValid && !currentState.isLoading

        setState {
            copy(
                isNameValid = isNameValid,
                isDateValid = isDateValid,
                canContinue = canContinue
            )
        }
    }
}