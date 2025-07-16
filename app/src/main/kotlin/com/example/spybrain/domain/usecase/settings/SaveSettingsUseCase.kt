package com.example.spybrain.domain.usecase.settings

import com.example.spybrain.domain.model.Settings
import com.example.spybrain.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * UseCase РґР»СЏ СЃРѕС…СЂР°РЅРµРЅРёСЏ РЅР°СЃС‚СЂРѕРµРє РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
class SaveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    /**
     * РЎРѕС…СЂР°РЅСЏРµС‚ РЅР°СЃС‚СЂРѕР№РєРё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @param settings РќР°СЃС‚СЂРѕР№РєРё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    suspend operator fun invoke(settings: Settings) = settingsRepository.saveSettings(settings)
}
