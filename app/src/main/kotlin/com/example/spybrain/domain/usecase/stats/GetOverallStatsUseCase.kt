package com.example.spybrain.domain.usecase.stats

import com.example.spybrain.domain.model.Stats
import com.example.spybrain.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РѕР±С‰РµР№ СЃС‚Р°С‚РёСЃС‚РёРєРё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
class GetOverallStatsUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    /**
     * РџРѕР»СѓС‡Р°РµС‚ РѕР±С‰СѓСЋ СЃС‚Р°С‚РёСЃС‚РёРєСѓ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РћР±С‰Р°СЏ СЃС‚Р°С‚РёСЃС‚РёРєР°.
     */
    operator fun invoke(): Flow<Stats> = statsRepository.getOverallStats()
}
