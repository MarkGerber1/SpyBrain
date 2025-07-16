package com.example.spybrain.domain.usecase.stats

import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.repository.StatsRepository
import javax.inject.Inject

/**
 * UseCase РґР»СЏ СЃРѕС…СЂР°РЅРµРЅРёСЏ СЃРµСЃСЃРёРё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
class SaveSessionUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    /**
     * РЎРѕС…СЂР°РЅСЏРµС‚ СЃРµСЃСЃРёСЋ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @param session РЎРµСЃСЃРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    suspend operator fun invoke(session: Session) {
        statsRepository.saveSession(session)
    }
}
