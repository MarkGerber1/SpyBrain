package com.example.spybrain.presentation.reminders

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.lifecycle.viewModelScope
import com.example.spybrain.data.repository.HeartRateRepository
import com.example.spybrain.presentation.base.BaseViewModel
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeartRateViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val heartRateRepository: HeartRateRepository
) : BaseViewModel<HeartRateContract.Event, HeartRateContract.State, HeartRateContract.Effect>() {

    private var measurementJob: kotlinx.coroutines.Job? = null

    init {
        loadInitialData()
    }

    override fun createInitialState(): HeartRateContract.State = HeartRateContract.State()

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                val history = heartRateRepository.getMeasurementHistory()
                val points = heartRateRepository.getMotivationalPoints()
                
                setState { 
                    copy(
                        measurementHistory = history,
                        motivationalPoints = points
                    )
                }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
            }
        }
    }

    override fun handleEvent(event: HeartRateContract.Event) {
        when (event) {
            is HeartRateContract.Event.StartMeasurement -> {
                startHeartRateMeasurement()
            }
            is HeartRateContract.Event.StopMeasurement -> {
                stopHeartRateMeasurement()
            }
            is HeartRateContract.Event.ShowHistory -> {
                setEffect { HeartRateContract.Effect.NavigateTo(Screen.Stats) }
            }
            is HeartRateContract.Event.AddMotivationalPoint -> {
                addMotivationalPoint()
            }
            is HeartRateContract.Event.MeasurementCompleted -> {
                handleMeasurementCompleted(event.heartRate)
            }
            is HeartRateContract.Event.Error -> {
                setState { copy(error = event.message) }
            }
        }
    }

    private fun startHeartRateMeasurement() {
        if (uiState.value.isMeasuring) return

        setState { copy(isMeasuring = true, error = null) }
        
        measurementJob = viewModelScope.launch {
            try {
                // Симуляция измерения пульса через камеру
                // В реальном приложении здесь будет логика работы с Camera2 API
                simulateHeartRateMeasurement()
            } catch (e: Exception) {
                setState { 
                    copy(
                        isMeasuring = false,
                        error = "Ошибка измерения: ${e.message}"
                    )
                }
                setEffect { HeartRateContract.Effect.ShowToast("Ошибка измерения пульса") }
            }
        }
    }

    private suspend fun simulateHeartRateMeasurement() {
        // Симуляция процесса измерения
        delay(2000) // Задержка для "подготовки" камеры
        
        var measurementCount = 0
        val maxMeasurements = 10
        
        while (uiState.value.isMeasuring && measurementCount < maxMeasurements) {
            // Генерируем случайный пульс в диапазоне 60-100
            val simulatedHeartRate = (60..100).random()
            
            setState { copy(currentHeartRate = simulatedHeartRate) }
            
            delay(1000) // Измерение каждую секунду
            measurementCount++
        }
        
        if (uiState.value.isMeasuring) {
            // Завершаем измерение
            val finalHeartRate = uiState.value.currentHeartRate
            handleMeasurementCompleted(finalHeartRate)
        }
    }

    private fun stopHeartRateMeasurement() {
        measurementJob?.cancel()
        setState { copy(isMeasuring = false) }
        setEffect { HeartRateContract.Effect.ShowToast("Измерение остановлено") }
    }

    private fun handleMeasurementCompleted(heartRate: Int) {
        viewModelScope.launch {
            try {
                // Сохраняем измерение
                heartRateRepository.saveMeasurement(heartRate)
                
                // Добавляем мотивационный балл
                addMotivationalPoint()
                
                // Обновляем историю
                val updatedHistory = heartRateRepository.getMeasurementHistory()
                
                setState { 
                    copy(
                        isMeasuring = false,
                        currentHeartRate = heartRate,
                        measurementHistory = updatedHistory
                    )
                }
                
                setEffect { HeartRateContract.Effect.ShowToast("Измерение завершено: $heartRate уд/мин") }
                
            } catch (e: Exception) {
                setState { 
                    copy(
                        isMeasuring = false,
                        error = "Ошибка сохранения: ${e.message}"
                    )
                }
            }
        }
    }

    private fun addMotivationalPoint() {
        viewModelScope.launch {
            try {
                val newPoints = heartRateRepository.addMotivationalPoint()
                val shouldShowUnlock = newPoints % 10 == 0 // Показываем уведомление каждые 10 баллов
                
                setState { 
                    copy(
                        motivationalPoints = newPoints,
                        showNewExerciseUnlocked = shouldShowUnlock
                    )
                }
                
                if (shouldShowUnlock) {
                    setEffect { HeartRateContract.Effect.ShowToast("Открыто новое упражнение!") }
                    
                    // Скрываем уведомление через 5 секунд
                    delay(5000)
                    setState { copy(showNewExerciseUnlocked = false) }
                }
                
            } catch (e: Exception) {
                setState { copy(error = "Ошибка обновления баллов: ${e.message}") }
            }
        }
    }
} 