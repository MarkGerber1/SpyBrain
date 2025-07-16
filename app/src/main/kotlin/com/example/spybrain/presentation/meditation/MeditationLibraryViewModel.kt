package com.example.spybrain.presentation.meditation

import androidx.lifecycle.viewModelScope
import com.example.spybrain.domain.model.MeditationProgram
import com.example.spybrain.domain.usecase.meditation.GetMeditationProgramsUseCase
import com.example.spybrain.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.spybrain.service.AiMentorService
import javax.inject.Inject
import com.example.spybrain.domain.error.ErrorHandler // FIXME Р±РёР»Рґ-С„РёРєСЃ 09.05.2025
import com.example.spybrain.domain.service.IPlayerService
import com.example.spybrain.domain.service.IAiMentor
import com.example.spybrain.util.UiError
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.spybrain.presentation.meditation.MeditationLibraryContract
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEffect

@HiltViewModel
class MeditationLibraryViewModel @Inject constructor(
    private val getProgramsUseCase: GetMeditationProgramsUseCase,
    private val playerService: IPlayerService, // TODO СЂРµР°Р»РёР·РѕРІР°РЅРѕ: РІРЅРµРґСЂРµРЅРёРµ С‡РµСЂРµР· Р°Р±СЃС‚СЂР°РєС†РёСЋ
    private val aiMentor: IAiMentor // TODO СЂРµР°Р»РёР·РѕРІР°РЅРѕ: РІРЅРµРґСЂРµРЅРёРµ С‡РµСЂРµР· Р°Р±СЃС‚СЂР°РєС†РёСЋ
) : BaseViewModel<
    MeditationLibraryContract.Event,
    MeditationLibraryContract.State,
    MeditationLibraryContract.Effect
>() { // TODO: Добавить все необходимые юнит-тесты д

    init {
        setEvent(MeditationLibraryContract.Event.LoadPrograms)
    }

    override fun createInitialState(): MeditationLibraryContract.State = MeditationLibraryContract.State()

    override fun handleEvent(event: MeditationLibraryContract.Event) {
        when (event) {
            MeditationLibraryContract.Event.LoadPrograms -> loadPrograms()
        }
    }

    private fun loadPrograms() {
        viewModelScope.launch {
            getProgramsUseCase()
                .onStart { setState { copy(error = null) } }
                .catch { e -> setEffect { MeditationLibraryContract.Effect.ShowError(ErrorHandler.mapToUiError(ErrorHandler.handle(e))) } }
                .collect { programs ->
                    setState { copy(programs = programs) }
                }
        }
    }

    fun playProgram(program: MeditationProgram) {
        playerService.stop()
        playerService.play(program.audioUrl)
        aiMentor.giveMeditationAdvice()
    }

    override fun onCleared() {
        playerService.release()
        super.onCleared()
    }
}
