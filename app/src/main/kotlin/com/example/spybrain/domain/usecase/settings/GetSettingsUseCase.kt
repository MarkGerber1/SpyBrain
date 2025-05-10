package com.example.spybrain.domain.usecase.settings

import com.example.spybrain.domain.model.Settings
import com.example.spybrain.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Settings> = settingsRepository.getSettings()
} 