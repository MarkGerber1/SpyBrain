package com.example.spybrain.domain.usecase.breathing

import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.domain.repository.CustomBreathingPatternRepository
import javax.inject.Inject

/**
 * Use Case для добавления пользовательского шаблона дыхания.
 */
class AddCustomBreathingPatternUseCase @Inject constructor(
    private val repository: CustomBreathingPatternRepository
) {
    suspend operator fun invoke(pattern: CustomBreathingPattern) {
        repository.addCustomPattern(pattern)
    }
} 