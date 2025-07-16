package com.example.spybrain.domain.usecase.breathing

import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.domain.repository.CustomBreathingPatternRepository
import javax.inject.Inject

/**
 * Use Case РґР»СЏ РґРѕР±Р°РІР»РµРЅРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРѕРіРѕ С€Р°Р±Р»РѕРЅР° РґС‹С…Р°РЅРёСЏ.
 */
class AddCustomBreathingPatternUseCase @Inject constructor(
    private val repository: CustomBreathingPatternRepository
) {
    /**
     * Р”РѕР±Р°РІР»СЏРµС‚ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёР№ С€Р°Р±Р»РѕРЅ РґС‹С…Р°РЅРёСЏ.
     * @param pattern РќРѕРІС‹Р№ С€Р°Р±Р»РѕРЅ РґС‹С…Р°РЅРёСЏ.
     */
    suspend operator fun invoke(pattern: CustomBreathingPattern) {
        repository.add(pattern)
    }
}
