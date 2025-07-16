package com.example.spybrain.domain.usecase.achievements

import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.domain.repository.AchievementsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РґРѕСЃС‚РёР¶РµРЅРёР№ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
class GetAchievementsUseCase @Inject constructor(
    private val repository: AchievementsRepository
) {
    /**
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє РґРѕСЃС‚РёР¶РµРЅРёР№ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @param userId РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РЎРїРёСЃРѕРє РґРѕСЃС‚РёР¶РµРЅРёР№.
     */
    operator fun invoke(): Flow<List<Achievement>> = repository.getAchievements()
}
