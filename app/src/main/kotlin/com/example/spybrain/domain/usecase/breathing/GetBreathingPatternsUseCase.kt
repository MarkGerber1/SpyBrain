package com.example.spybrain.domain.usecase.breathing

import com.example.spybrain.domain.model.BreathingPattern
import com.example.spybrain.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
 */
class GetBreathingPatternsUseCase @Inject constructor(
    private val breathingRepository: BreathingRepository
) {
    /**
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
     * @return РЎРїРёСЃРѕРє РїР°С‚С‚РµСЂРЅРѕРІ.
     */
    operator fun invoke(): Flow<List<BreathingPattern>> = breathingRepository.getBreathingPatterns()
}
