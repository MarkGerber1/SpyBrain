package com.example.spybrain.domain.usecase.settings

import com.example.spybrain.domain.model.Settings
import com.example.spybrain.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(settings: Settings) = settingsRepository.saveSettings(settings)
} 