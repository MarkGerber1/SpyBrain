package com.example.spybrain.domain.usecase.meditation

import com.example.spybrain.domain.model.MeditationProgram
import com.example.spybrain.domain.repository.MeditationProgramRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ СЃРїРёСЃРєР° С‚РµРјР°С‚РёС‡РµСЃРєРёС… РјРµРґРёС‚Р°С†РёРѕРЅРЅС‹С… РїСЂРѕРіСЂР°РјРј.
 */
class GetMeditationProgramsUseCase @Inject constructor(
    private val repository: MeditationProgramRepository
) {
    /**
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє РїСЂРѕРіСЂР°РјРј РјРµРґРёС‚Р°С†РёРё.
     * @return РЎРїРёСЃРѕРє РїСЂРѕРіСЂР°РјРј.
     */
    operator fun invoke(): Flow<List<MeditationProgram>> = repository.getMeditationPrograms()
}
