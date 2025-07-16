package com.example.spybrain.domain.usecase.profile

import com.example.spybrain.domain.model.Profile
import com.example.spybrain.domain.repository.ProfileRepository
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РѕР±РЅРѕРІР»РµРЅРёСЏ РїСЂРѕС„РёР»СЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
class UpdateUserProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    /**
     * РћР±РЅРѕРІР»СЏРµС‚ РїСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @param profile РџСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    suspend operator fun invoke(profile: Profile) {
        repository.saveProfile(profile)
    }
}
