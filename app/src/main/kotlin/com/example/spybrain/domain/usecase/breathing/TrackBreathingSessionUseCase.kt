package com.example.spybrain.domain.usecase.breathing

import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.repository.BreathingRepository
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РѕС‚СЃР»РµР¶РёРІР°РЅРёСЏ РґС‹С…Р°С‚РµР»СЊРЅРѕР№ СЃРµСЃСЃРёРё.
 */
class TrackBreathingSessionUseCase @Inject constructor(
    private val breathingRepository: BreathingRepository
) {
    /**
     * РћС‚СЃР»РµР¶РёРІР°РµС‚ РґС‹С…Р°С‚РµР»СЊРЅСѓСЋ СЃРµСЃСЃРёСЋ.
     * @param session РЎРµСЃСЃРёСЏ РґС‹С…Р°РЅРёСЏ.
     */
    suspend operator fun invoke(session: Session) {
        // Add validation or extra logic if needed
        breathingRepository.trackBreathingSession(session)
    }
}
