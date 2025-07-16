package com.example.spybrain.domain.usecase.stats

import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РёСЃС‚РѕСЂРёРё РІСЃРµС… СЃРµСЃСЃРёР№ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
class GetSessionHistoryUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    /**
     * РџРѕР»СѓС‡Р°РµС‚ РёСЃС‚РѕСЂРёСЋ РІСЃРµС… СЃРµСЃСЃРёР№ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РЎРїРёСЃРѕРє СЃРµСЃСЃРёР№.
     */
    operator fun invoke(): Flow<List<Session>> = statsRepository.getSessionHistory()
}
