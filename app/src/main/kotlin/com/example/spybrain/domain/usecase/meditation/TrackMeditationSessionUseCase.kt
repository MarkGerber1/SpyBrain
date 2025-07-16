package com.example.spybrain.domain.usecase.meditation

import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.repository.MeditationRepository
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РѕС‚СЃР»РµР¶РёРІР°РЅРёСЏ РјРµРґРёС‚Р°С†РёРѕРЅРЅРѕР№ СЃРµСЃСЃРёРё.
 */
class TrackMeditationSessionUseCase @Inject constructor(
    private val meditationRepository: MeditationRepository
) {
    /**
     * РћС‚СЃР»РµР¶РёРІР°РµС‚ РјРµРґРёС‚Р°С†РёРѕРЅРЅСѓСЋ СЃРµСЃСЃРёСЋ.
     * @param session РЎРµСЃСЃРёСЏ РјРµРґРёС‚Р°С†РёРё.
     */
    suspend operator fun invoke(session: Session) {
        // Add validation or extra logic if needed
        meditationRepository.trackMeditationSession(session)
    }
}
