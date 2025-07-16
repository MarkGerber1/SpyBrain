package com.example.spybrain.domain.usecase.meditation

import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.domain.repository.MeditationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ СЃРїРёСЃРєР° РјРµРґРёС‚Р°С†РёР№.
 */
class GetMeditationsUseCase @Inject constructor(
    private val meditationRepository: MeditationRepository
) {
    /**
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє РјРµРґРёС‚Р°С†РёР№.
     * @return РЎРїРёСЃРѕРє РјРµРґРёС‚Р°С†РёР№.
     */
    operator fun invoke(): Flow<List<Meditation>> = meditationRepository.getMeditations()
}
