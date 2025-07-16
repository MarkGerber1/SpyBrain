package com.example.spybrain.presentation.profile

import com.example.spybrain.domain.model.Profile
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.util.UiError

/**
 */
object ProfileContract {
    /**
     */
    data class State(
        val isLoading: Boolean = false,
        val profile: Profile? = null,
        val showDialog: Boolean = false,
        val newName: String = "",
        val error: UiError? = null
    ) : UiState

    /**
     */
    sealed class Event : UiEvent {
        /**
         */
        object LoadProfile : Event()
        object EditNameClicked : Event()
        data class NameChanged(val name: String) : Event()
        object SaveName : Event()
        object DismissDialog : Event()
    }

    /**
     */
    sealed class Effect : UiEffect {
        /**
         */
        data class ShowError(val error: UiError) : Effect()
    }
}
