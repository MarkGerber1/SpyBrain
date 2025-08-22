package com.example.spybrain.presentation.home

import androidx.lifecycle.viewModelScope
import com.example.spybrain.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.spybrain.domain.usecase.profile.GetUserProfileUseCase
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEffect
import java.util.Calendar
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.spybrain.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.first

object HomeContract {
    data class State(
        val userName: String = "",
        val welcomeText: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Event : UiEvent {
        object Load : Event()
    }

    sealed class Effect : UiEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    @ApplicationContext private val appContext: Context
) : BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    override fun createInitialState() = HomeContract.State()

    init {
        setEvent(HomeContract.Event.Load)
    }

    override fun handleEvent(event: HomeContract.Event) {
        when (event) {
            HomeContract.Event.Load -> load()
        }
    }

    private fun load() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val settings = SettingsDataStore(appContext)
            val fallbackName = settings.userNameFlow.first().ifBlank { "Друг" }
            getUserProfile().collect { profile ->
                val greeting = timeGreeting()
                val name = profile.name.ifBlank { fallbackName }
                setState {
                    copy(
                        isLoading = false,
                        userName = name,
                        welcomeText = "$greeting, ${'$'}name!"
                    )
                }
            }
        }
    }

    private fun timeGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 6 -> "Доброй ночи"
            hour < 12 -> "Доброго утра"
            hour < 17 -> "Доброго дня"
            else -> "Доброго вечера"
        }
    }
}