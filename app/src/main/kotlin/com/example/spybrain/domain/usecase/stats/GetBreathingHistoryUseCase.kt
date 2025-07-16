package com.example.spybrain.domain.usecase.stats

import com.example.spybrain.domain.model.BreathingSession
import com.example.spybrain.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РёСЃС‚РѕСЂРёРё РґС‹С…Р°С‚РµР»СЊРЅС‹С… СЃРµСЃСЃРёР№.
 */
class GetBreathingHistoryUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    /**
     * РџРѕР»СѓС‡Р°РµС‚ РёСЃС‚РѕСЂРёСЋ РґС‹С…Р°С‚РµР»СЊРЅС‹С… СЃРµСЃСЃРёР№.
     * @return РЎРїРёСЃРѕРє РґС‹С…Р°С‚РµР»СЊРЅС‹С… СЃРµСЃСЃРёР№.
     */
    operator fun invoke(): Flow<List<BreathingSession>> = statsRepository.getBreathingHistory()
}
