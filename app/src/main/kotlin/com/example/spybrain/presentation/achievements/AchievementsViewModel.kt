package com.example.spybrain.presentation.achievements

import androidx.lifecycle.viewModelScope
import com.example.spybrain.domain.usecase.achievements.GetAchievementsUseCase
import com.example.spybrain.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import com.example.spybrain.presentation.achievements.AchievementsContract
import javax.inject.Inject
import com.example.spybrain.domain.error.ErrorHandler // FIXME билд-фикс 09.05.2025

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val getAchievementsUseCase: GetAchievementsUseCase
) : BaseViewModel<AchievementsContract.Event, AchievementsContract.State, AchievementsContract.Effect>() {

    // FIXME: Отсутствует UI экран для отображения достижений. Необходимо реализовать Composable Screen для Achievements.

    init {
        setEvent(AchievementsContract.Event.LoadAchievements)
    }

    override fun createInitialState(): AchievementsContract.State = AchievementsContract.State()

    override fun handleEvent(event: AchievementsContract.Event) {
        when (event) {
            AchievementsContract.Event.LoadAchievements -> loadAchievements()
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            getAchievementsUseCase()
                .onStart { setState { copy(error = null) } }
                .catch { e -> setEffect { AchievementsContract.Effect.ShowError(ErrorHandler.mapToUiError(ErrorHandler.handle(e))) } }
                .collect { list -> setState { copy(achievements = list) } }
        }
    }
} 