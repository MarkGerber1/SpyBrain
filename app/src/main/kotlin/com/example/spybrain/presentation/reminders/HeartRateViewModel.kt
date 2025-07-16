package com.example.spybrain.presentation.reminders

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.lifecycle.viewModelScope
import com.example.spybrain.data.repository.HeartRateRepository
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.presentation.navigation.Screen
import com.example.spybrain.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.spybrain.presentation.reminders.HeartRateContract

/**
 * @constructor Р’РЅРµРґСЂРµРЅРёРµ Р·Р°РІРёСЃРёРјРѕСЃС‚РµР№ С‡РµСЂРµР· Hilt.
 * @param context РљРѕРЅС‚РµРєСЃС‚ РїСЂРёР»РѕР¶РµРЅРёСЏ.
 * @param heartRateRepository Р РµРїРѕР·РёС‚РѕСЂРёР№ РїСѓР»СЊСЃРѕРјРµС‚СЂР°.
 */
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
                // РЎРёРјСѓР»СЏС†РёСЏ РёР·РјРµСЂРµРЅРёСЏ РїСѓР»СЊСЃР° С‡РµСЂРµР· РєР°РјРµСЂСѓ
                // Р’ СЂРµР°Р»СЊРЅРѕРј РїСЂРёР»РѕР¶РµРЅРёРё Р·РґРµСЃСЊ Р±СѓРґРµС‚ Р»РѕРіРёРєР° СЂР°Р±РѕС‚С‹ СЃ Camera2 API
                simulateHeartRateMeasurement()
            } catch (e: Exception) {
                setState {
                    copy(
                        isMeasuring = false,
                        error = "РћС€РёР±РєР° РёР·РјРµСЂРµРЅРёСЏ: ${e.message}"
                    )
                }
                setEffect { HeartRateContract.Effect.ShowToast("РћС€РёР±РєР° РёР·РјРµСЂРµРЅРёСЏ РїСѓР»СЊСЃР°") }
            }
        }
    }

    private suspend fun simulateHeartRateMeasurement() {
        // РЎРёРјСѓР»СЏС†РёСЏ РїСЂРѕС†РµСЃСЃР° РёР·РјРµСЂРµРЅРёСЏ
        delay(2000) // Р—Р°РґРµСЂР¶РєР° РґР»СЏ "РїРѕРґРіРѕС‚РѕРІРєРё" РєР°РјРµСЂС‹

        var measurementCount = 0
        val maxMeasurements = 10

        while (uiState.value.isMeasuring && measurementCount < maxMeasurements) {
            // Р“РµРЅРµСЂРёСЂСѓРµРј СЃР»СѓС‡Р°Р№РЅС‹Р№ РїСѓР»СЊСЃ РІ РґРёР°РїР°Р·РѕРЅРµ 60-100
            val simulatedHeartRate = (60..100).random()

            setState { copy(currentHeartRate = simulatedHeartRate) }

            delay(1000) // РР·РјРµСЂРµРЅРёРµ РєР°Р¶РґСѓСЋ СЃРµРєСѓРЅРґСѓ
            measurementCount++
        }

        if (uiState.value.isMeasuring) {
            // Р—Р°РІРµСЂС€Р°РµРј РёР·РјРµСЂРµРЅРёРµ
            val finalHeartRate = uiState.value.currentHeartRate
            handleMeasurementCompleted(finalHeartRate)
        }
    }

    private fun stopHeartRateMeasurement() {
        measurementJob?.cancel()
        setState { copy(isMeasuring = false) }
        setEffect { HeartRateContract.Effect.ShowToast("РР·РјРµСЂРµРЅРёРµ РѕСЃС‚Р°РЅРѕРІР»РµРЅРѕ") }
    }

    private fun handleMeasurementCompleted(heartRate: Int) {
        viewModelScope.launch {
            try {
                // РЎРѕС…СЂР°РЅСЏРµРј РёР·РјРµСЂРµРЅРёРµ
                heartRateRepository.saveMeasurement(heartRate)

                // Р”РѕР±Р°РІР»СЏРµРј РјРѕС‚РёРІР°С†РёРѕРЅРЅС‹Р№ Р±Р°Р»Р»
                addMotivationalPoint()

                // РћР±РЅРѕРІР»СЏРµРј РёСЃС‚РѕСЂРёСЋ
                val updatedHistory = heartRateRepository.getMeasurementHistory()

                setState {
                    copy(
                        isMeasuring = false,
                        currentHeartRate = heartRate,
                        measurementHistory = updatedHistory
                    )
                }

                setEffect { HeartRateContract.Effect.ShowToast("РР·РјРµСЂРµРЅРёРµ Р·Р°РІРµСЂС€РµРЅРѕ: $heartRate СѓРґ/РјРёРЅ") }

            } catch (e: Exception) {
                setState {
                    copy(
                        isMeasuring = false,
                        error = "РћС€РёР±РєР° СЃРѕС…СЂР°РЅРµРЅРёСЏ: ${e.message}"
                    )
                }
            }
        }
    }

    private fun addMotivationalPoint() {
        viewModelScope.launch {
            try {
                val newPoints = heartRateRepository.addMotivationalPoint()
                val shouldShowUnlock = newPoints % 10 == 0 // РџРѕРєР°Р·С‹РІР°РµРј СѓРІРµРґРѕРјР»РµРЅРёРµ РєР°Р¶РґС‹Рµ 10 Р±Р°Р»Р»РѕРІ

                setState {
                    copy(
                        motivationalPoints = newPoints,
                        showNewExerciseUnlocked = shouldShowUnlock
                    )
                }

                if (shouldShowUnlock) {
                    setEffect { HeartRateContract.Effect.ShowToast("РћС‚РєСЂС‹С‚Рѕ РЅРѕРІРѕРµ СѓРїСЂР°Р¶РЅРµРЅРёРµ!") }

                    // РЎРєСЂС‹РІР°РµРј СѓРІРµРґРѕРјР»РµРЅРёРµ С‡РµСЂРµР· 5 СЃРµРєСѓРЅРґ
                    delay(5000)
                    setState { copy(showNewExerciseUnlocked = false) }
                }

            } catch (e: Exception) {
                setState { copy(error = "РћС€РёР±РєР° РѕР±РЅРѕРІР»РµРЅРёСЏ Р±Р°Р»Р»РѕРІ: ${e.message}") }
            }
        }
    }
}
