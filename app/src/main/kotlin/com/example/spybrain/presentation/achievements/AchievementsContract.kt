package com.example.spybrain.presentation.achievements

import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.util.UiError

object AchievementsContract {
    data class State(
        val achievements: List<Achievement> = emptyList(),
        val error: UiError? = null // TODO: Заменить на UiError sealed class для централизованной обработки ошибок и поддержки локализации
    ) : UiState

    sealed class Event : UiEvent {
        object LoadAchievements : Event()
    }

    sealed class Effect : UiEffect {
        data class ShowError(val error: UiError) : Effect()
    }
} 