package com.example.spybrain.data.repository

import com.example.spybrain.domain.model.BreathingPattern
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import com.example.spybrain.data.model.toBreathingEntity
import com.example.spybrain.data.model.toDomain
import com.example.spybrain.data.storage.dao.BreathingSessionDao
import com.example.spybrain.data.storage.dao.CustomBreathingPatternDao
import com.example.spybrain.data.model.CustomBreathingPatternEntity
import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.data.model.BreathingSessionEntity

/**
 * Р РµР°Р»РёР·Р°С†РёСЏ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїСЂР°РєС‚РёРє.
 * @constructor Р’РЅРµРґСЂСЏРµС‚ DAO РґР»СЏ СЃРµСЃСЃРёР№ РґС‹С…Р°РЅРёСЏ Рё РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёС… РїР°С‚С‚РµСЂРЅРѕРІ.
 */
@Singleton
class BreathingRepositoryImpl @Inject constructor(
    private val dao: BreathingSessionDao,
    private val customDao: CustomBreathingPatternDao
) : BreathingRepository {

    override fun getBreathingPatterns(): Flow<List<BreathingPattern>> {
        // РЎС‚Р°РЅРґР°СЂС‚РЅС‹Рµ С€Р°Р±Р»РѕРЅС‹
        val dummyPatterns = listOf(
            BreathingPattern(
                id = "br1",
                name = "Box Breathing",
                voicePrompt = "Box Breathing",
                description = "4-4-4-4 pattern",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 4,
                exhaleSeconds = 4,
                holdAfterExhaleSeconds = 4,
                totalCycles = 10
            ),
            BreathingPattern(
                id = "br2",
                name = "Relaxing Breath",
                voicePrompt = "Relaxing Breath",
                description = "4-7-8 pattern",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 7,
                exhaleSeconds = 8,
                holdAfterExhaleSeconds = 0,
                totalCycles = 8
            ),
            BreathingPattern(
                id = "br3",
                name = "Calm Breath",
                voicePrompt = "Calm Breath",
                description = "Calm breathing pattern",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 2,
                exhaleSeconds = 6,
                holdAfterExhaleSeconds = 2,
                totalCycles = 6
            )
        )
        // РџРѕС‚РѕРє РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёС… С€Р°Р±Р»РѕРЅРѕРІ, РјР°РїРёРј РЅР° BreathingPattern
        val customFlow = customDao.getAllPatterns().map { entities ->
            entities.map { entity ->
                val cp = entity.toDomain()
                BreathingPattern(
                    id = cp.id.toString(),
                    name = cp.name,
                    voicePrompt = cp.name,
                    description = cp.description.orEmpty(),
                    inhaleSeconds = cp.inhaleSeconds,
                    holdAfterInhaleSeconds = cp.holdAfterInhaleSeconds,
                    exhaleSeconds = cp.exhaleSeconds,
                    holdAfterExhaleSeconds = cp.holdAfterExhaleSeconds,
                    totalCycles = cp.totalCycles
                )
            }
        }
        // РћР±СЉРµРґРёРЅСЏРµРј СЃС‚Р°РЅРґР°СЂС‚РЅС‹Рµ Рё РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёРµ С€Р°Р±Р»РѕРЅС‹
        return flowOf(dummyPatterns).combine(customFlow) { default, custom -> default + custom }
    }

    override fun getBreathingPatternById(id: String): Flow<BreathingPattern?> =
        getBreathingPatterns().map { list -> list.firstOrNull { it.id == id } }

    override suspend fun trackBreathingSession(session: Session) {
        dao.insert(session.toBreathingEntity())
    }
}
