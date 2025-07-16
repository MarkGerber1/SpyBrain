package com.example.spybrain.presentation.reminders

import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.presentation.navigation.Screen
import com.example.spybrain.data.storage.model.HeartRateMeasurement

/**
 */
object HeartRateContract {

    /**
     */
    data class State(
        val isMeasuring: Boolean = false,
        val currentHeartRate: Int = 0,
        val measurementHistory: List<HeartRateMeasurement> = emptyList(),
        val motivationalPoints: Int = 0,
        val showNewExerciseUnlocked: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    /**
     */
    sealed class Event : UiEvent {
        /**
         */
        object StartMeasurement : Event()
        /**
         */
        object StopMeasurement : Event()
        /**
         */
        object ShowHistory : Event()
        /**
         */
        object AddMotivationalPoint : Event()
        /**
         */
        data class MeasurementCompleted(val heartRate: Int) : Event()
        /**
         */
        data class Error(val message: String) : Event()
    }

    /**
     */
    sealed class Effect : UiEffect {
        /** */
        data class ShowToast(val message: String) : Effect()
        /** */
        data class NavigateTo(val screen: Screen) : Effect()
    }
}
