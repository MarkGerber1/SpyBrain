package com.example.spybrain.domain.usecase.stats

import com.example.spybrain.domain.model.BreathingSession
import com.example.spybrain.domain.repository.StatsRepository
import javax.inject.Inject

/**
 * UseCase РґР»СЏ СЃРѕС…СЂР°РЅРµРЅРёСЏ РґС‹С…Р°С‚РµР»СЊРЅРѕР№ СЃРµСЃСЃРёРё.
 */
class SaveBreathingSessionUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    /**
     * РЎРѕС…СЂР°РЅСЏРµС‚ РґС‹С…Р°С‚РµР»СЊРЅСѓСЋ СЃРµСЃСЃРёСЋ.
     * @param session Р”С‹С…Р°С‚РµР»СЊРЅР°СЏ СЃРµСЃСЃРёСЏ.
     */
    suspend operator fun invoke(session: BreathingSession) {
        statsRepository.saveBreathingSession(session)
    }
}
