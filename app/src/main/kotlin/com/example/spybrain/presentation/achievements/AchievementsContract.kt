package com.example.spybrain.presentation.achievements

import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.domain.model.AchievementType
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.util.UiError

object AchievementsContract {
    data class State(
        val achievements: List<Achievement> = emptyList(),
        val selectedTab: AchievementType = AchievementType.GENERAL,
        val totalPoints: Int = 0,
        val userLevel: UserLevel = UserLevel(1, 0, 100, 0f),
        val isLoading: Boolean = false,
        val error: UiError? = null // TODO: Заменить на UiError sealed class для централизованной обработки ошибок и поддержки локализации
    ) : UiState

    sealed class Event : UiEvent {
        object LoadAchievements : Event()
        data class TabSelected(val type: AchievementType) : Event()
        data class AchievementClicked(val achievement: Achievement) : Event()
    }

    sealed class Effect : UiEffect {
        data class ShowError(val error: UiError) : Effect()
        data class ShowAchievementDetails(val achievement: Achievement) : Effect()
        data class ShowToast(val message: String) : Effect()
    }
    
    data class UserLevel(
        val level: Int,
        val currentPoints: Int,
        val requiredPoints: Int,
        val progress: Float
    )
} 