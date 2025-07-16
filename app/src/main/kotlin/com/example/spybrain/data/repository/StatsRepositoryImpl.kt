package com.example.spybrain.data.repository

import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.Stats
import com.example.spybrain.domain.model.BreathingSession
import com.example.spybrain.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import com.example.spybrain.data.storage.dao.MeditationSessionDao
import com.example.spybrain.data.storage.dao.BreathingSessionDao
import com.example.spybrain.data.model.toDomain
import com.example.spybrain.data.model.toEntity
import com.example.spybrain.data.model.BreathingSessionEntity

/**
 * Р РµР°Р»РёР·Р°С†РёСЏ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ СЃС‚Р°С‚РёСЃС‚РёРєРё.
 * @constructor Р’РЅРµРґСЂСЏРµС‚ DAO РґР»СЏ РјРµРґРёС‚Р°С†РёР№ Рё РґС‹С…Р°РЅРёСЏ.
 */
@Singleton
class StatsRepositoryImpl @Inject constructor(
    private val meditationDao: MeditationSessionDao,
    private val breathingDao: BreathingSessionDao
) : StatsRepository {

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РѕР±С‰СѓСЋ СЃС‚Р°С‚РёСЃС‚РёРєСѓ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return Flow СЃ РѕР±СЉРµРєС‚РѕРј Stats.
     */
    override fun getOverallStats(): Flow<Stats> =
        combine(
            meditationDao.getAllSessions(),
            breathingDao.getAllSessions()
        ) { medEntities, breathEntities ->
            val totalMeditation = medEntities.sumOf { it.durationSeconds }
            val totalBreathing = breathEntities.sumOf { it.durationSeconds }
            Stats(
                totalMeditationTimeSeconds = totalMeditation,
                totalBreathingTimeSeconds = totalBreathing,
                completedMeditationSessions = medEntities.size,
                completedBreathingSessions = breathEntities.size,
                currentStreakDays = 0,
                longestStreakDays = 0
            )
        }

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РёСЃС‚РѕСЂРёСЋ СЃРµСЃСЃРёР№ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return Flow СЃРѕ СЃРїРёСЃРєРѕРј СЃРµСЃСЃРёР№.
     */
    override fun getSessionHistory(): Flow<List<Session>> =
        meditationDao.getAllSessions().map { list ->
            list.map { it.toDomain() }
        }

    override fun getBreathingHistory(): Flow<List<BreathingSession>> =
        breathingDao.getAllSessions().map { list ->
            list.map { entity ->
                BreathingSession(
                    patternName = entity.patternName,
                    durationSeconds = entity.durationSeconds.toInt(),
                    completed = true,
                    timestamp = entity.timestamp
                )
            }
        }

    override suspend fun saveSession(session: Session) {
        meditationDao.insert(session.toEntity())
    }

    override suspend fun saveBreathingSession(session: BreathingSession) {
        breathingDao.insert(session.toEntity())
    }
}
