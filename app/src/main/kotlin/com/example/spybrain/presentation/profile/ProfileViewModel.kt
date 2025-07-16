package com.example.spybrain.presentation.profile

import androidx.lifecycle.viewModelScope
import com.example.spybrain.presentation.base.BaseViewModel
import com.example.spybrain.presentation.profile.ProfileContract
import com.example.spybrain.domain.usecase.profile.GetUserProfileUseCase
import com.example.spybrain.domain.usecase.profile.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.spybrain.util.UiError
import com.example.spybrain.domain.error.ErrorHandler // FIXME Р±РёР»Рґ-С„РёРєСЃ 09.05.2025
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEffect

/**
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) : BaseViewModel<ProfileContract.Event, ProfileContract.State, ProfileContract.Effect>() {

    override fun createInitialState() = ProfileContract.State()

    init {
        setEvent(ProfileContract.Event.LoadProfile)
    }

    override fun handleEvent(event: ProfileContract.Event) {
        when (event) {
            ProfileContract.Event.LoadProfile -> loadProfile()
            ProfileContract.Event.EditNameClicked -> setState {
                copy(
                    showDialog = true,
                    newName = uiState.value.profile?.name ?: ""
                )
            }
            is ProfileContract.Event.NameChanged -> setState { copy(newName = event.name) }
            ProfileContract.Event.SaveName -> saveName()
            ProfileContract.Event.DismissDialog -> setState { copy(showDialog = false) }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getUserProfileUseCase()
                .collect { profile ->
                    setState { copy(profile = profile) }
                }
        }
    }

    private fun saveName() {
        val newName = uiState.value.newName.trim()
        val oldProfile = uiState.value.profile
        if (oldProfile == null) {
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(Exception("РџСЂРѕС„РёР»СЊ РЅРµ Р·Р°РіСЂСѓР¶РµРЅ")))
            setEffect { ProfileContract.Effect.ShowError(uiError) }
            return
        }
        if (newName.isBlank()) {
            val uiError = ErrorHandler.mapToUiError(ErrorHandler.handle(Exception("РРјСЏ РЅРµ РјРѕР¶РµС‚ Р±С‹С‚СЊ РїСѓСЃС‚С‹Рј")))
            setEffect { ProfileContract.Effect.ShowError(uiError) }
            return
        }
        val updatedProfile = oldProfile.copy(name = newName)
        viewModelScope.launch {
            updateUserProfileUseCase(updatedProfile)
            setState { copy(profile = updatedProfile, showDialog = false) }
        }
    }
}
