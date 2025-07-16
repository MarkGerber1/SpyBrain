package com.example.spybrain.domain.usecase.settings

import com.example.spybrain.domain.model.Settings
import com.example.spybrain.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РЅР°СЃС‚СЂРѕРµРє РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    /**
     * РџРѕР»СѓС‡Р°РµС‚ РЅР°СЃС‚СЂРѕР№РєРё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РќР°СЃС‚СЂРѕР№РєРё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    operator fun invoke(): Flow<Settings> = settingsRepository.getSettings()
}
