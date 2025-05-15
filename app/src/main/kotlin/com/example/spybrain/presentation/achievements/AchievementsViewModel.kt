package com.example.spybrain.presentation.achievements

import androidx.lifecycle.viewModelScope
import com.example.spybrain.domain.model.AchievementType
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
            is AchievementsContract.Event.LoadAchievements -> loadAchievements()
            is AchievementsContract.Event.TabSelected -> selectTab(event.type)
            is AchievementsContract.Event.AchievementClicked -> showAchievementDetails(event.achievement)
        }
    }

    private fun selectTab(type: AchievementType) {
        setState { copy(selectedTab = type) }
    }
    
    private fun showAchievementDetails(achievement: com.example.spybrain.domain.model.Achievement) {
        setEffect { AchievementsContract.Effect.ShowAchievementDetails(achievement) }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            getAchievementsUseCase()
                .onStart { 
                    setState { copy(error = null, isLoading = true) }
                }
                .catch { e -> 
                    setEffect { AchievementsContract.Effect.ShowError(ErrorHandler.mapToUiError(ErrorHandler.handle(e))) }
                    setState { copy(isLoading = false) }
                }
                .collect { list -> 
                    setState { 
                        copy(
                            achievements = list,
                            isLoading = false,
                            // Заглушка для UserLevel и totalPoints, в реальном приложении должны загружаться из репозитория
                            totalPoints = list.filter { it.isUnlocked }.sumOf { it.points },
                            userLevel = AchievementsContract.UserLevel(
                                level = calculateLevel(list),
                                currentPoints = list.filter { it.isUnlocked }.sumOf { it.points },
                                requiredPoints = 100,
                                progress = 0.5f
                            )
                        ) 
                    }
                }
        }
    }
    
    // Временная функция для вычисления уровня на основе достижений
    private fun calculateLevel(achievements: List<com.example.spybrain.domain.model.Achievement>): Int {
        val points = achievements.filter { it.isUnlocked }.sumOf { it.points }
        return when {
            points < 100 -> 1
            points < 300 -> 2
            points < 600 -> 3
            points < 1000 -> 4
            else -> 5
        }
    }
} 