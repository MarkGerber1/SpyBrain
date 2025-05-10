package com.example.spybrain.domain.usecase.profile

import com.example.spybrain.domain.model.Profile
import com.example.spybrain.domain.repository.ProfileRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: Profile) = profileRepository.saveProfile(profile)
} 