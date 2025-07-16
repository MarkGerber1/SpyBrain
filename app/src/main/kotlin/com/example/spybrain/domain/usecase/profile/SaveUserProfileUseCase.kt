package com.example.spybrain.domain.usecase.profile

import com.example.spybrain.domain.model.Profile
import com.example.spybrain.domain.repository.ProfileRepository
import javax.inject.Inject

/**
 * UseCase РґР»СЏ СЃРѕС…СЂР°РЅРµРЅРёСЏ РїСЂРѕС„РёР»СЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
class SaveUserProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    /**
     * РЎРѕС…СЂР°РЅСЏРµС‚ РїСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @param profile РџСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    suspend operator fun invoke(profile: Profile) = profileRepository.saveProfile(profile)
}
