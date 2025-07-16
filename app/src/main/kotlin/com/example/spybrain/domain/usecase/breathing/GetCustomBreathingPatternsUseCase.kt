package com.example.spybrain.domain.usecase.breathing

import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.domain.repository.CustomBreathingPatternRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЃРєРёС… РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
 */
class GetCustomBreathingPatternsUseCase @Inject constructor(
    private val repository: CustomBreathingPatternRepository
) {
    /**
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє РїРѕР»СЊР·РѕРІР°С‚РµР»СЃРєРёС… РїР°С‚С‚РµСЂРЅРѕРІ.
     * @return РЎРїРёСЃРѕРє РїР°С‚С‚РµСЂРЅРѕРІ.
     */
    operator fun invoke(): List<CustomBreathingPattern> = repository.getAll()
}
