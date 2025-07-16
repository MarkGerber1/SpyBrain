package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.MeditationProgram
import kotlinx.coroutines.flow.Flow

/**
 * Р РµРїРѕР·РёС‚РѕСЂРёР№ РґР»СЏ С‚РµРјР°С‚РёС‡РµСЃРєРёС… РјРµРґРёС‚Р°С†РёРѕРЅРЅС‹С… РїСЂРѕРіСЂР°РјРј.
 */
interface MeditationProgramRepository {
    /**
     * РџРѕР»СѓС‡РµРЅРёРµ СЃРїРёСЃРєР° РїСЂРѕРіСЂР°РјРј.
     */
    fun getMeditationPrograms(): Flow<List<MeditationProgram>>
}
