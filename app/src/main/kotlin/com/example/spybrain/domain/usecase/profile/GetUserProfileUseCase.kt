package com.example.spybrain.domain.usecase.profile

import com.example.spybrain.domain.model.Profile
import com.example.spybrain.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РїСЂРѕС„РёР»СЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
class GetUserProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    /**
     * РџРѕР»СѓС‡Р°РµС‚ РїСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РџСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    operator fun invoke(): Flow<Profile> = repository.getProfile()
}
