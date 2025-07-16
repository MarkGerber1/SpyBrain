package com.example.spybrain.domain.usecase.breathing

import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.domain.repository.CustomBreathingPatternRepository
import javax.inject.Inject

/**
 * Use Case РґР»СЏ СѓРґР°Р»РµРЅРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЃРєРѕРіРѕ С€Р°Р±Р»РѕРЅР° РґС‹С…Р°РЅРёСЏ.
 */
class DeleteCustomBreathingPatternUseCase @Inject constructor(
    private val repository: CustomBreathingPatternRepository
) {
    /**
     * Удаляет пользовательский шаблон дыхания.
     * @param id Идентификатор шаблона для удаления.
     */
    suspend operator fun invoke(id: Long) {
        repository.delete(id)
    }
}
