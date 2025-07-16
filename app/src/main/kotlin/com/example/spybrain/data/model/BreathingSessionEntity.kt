package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.SessionType
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.BreathingSession
import java.time.*
import java.time.Instant
import java.time.ZoneId

/**
 * РЎСѓС‰РЅРѕСЃС‚СЊ РґР»СЏ С…СЂР°РЅРµРЅРёСЏ СЃРµСЃСЃРёР№ РґС‹С…Р°РЅРёСЏ.
 * @property id РЈРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ.
 * @property patternName РќР°Р·РІР°РЅРёРµ РїР°С‚С‚РµСЂРЅР° РґС‹С…Р°РЅРёСЏ.
 * @property durationSeconds Р”Р»РёС‚РµР»СЊРЅРѕСЃС‚СЊ СЃРµСЃСЃРёРё (СЃРµРєСѓРЅРґС‹).
 * @property timestamp Р’СЂРµРјСЏ СЃРѕР·РґР°РЅРёСЏ (timestamp).
 */
@Entity(tableName = "breathing_sessions")
data class BreathingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    /** РќР°Р·РІР°РЅРёРµ РїР°С‚С‚РµСЂРЅР° РґС‹С…Р°РЅРёСЏ. */
    val patternName: String,
    /** Р”Р»РёС‚РµР»СЊРЅРѕСЃС‚СЊ СЃРµСЃСЃРёРё (СЃРµРєСѓРЅРґС‹). */
    val durationSeconds: Long,
    /** Р’СЂРµРјСЏ СЃРѕР·РґР°РЅРёСЏ (timestamp). */
    val timestamp: Long
)

// fun BreathingSessionEntity.toDomain() = com.example.spybrain.domain.model.BreathingSession(
//     id = id,
//     patternName = patternName,
//     durationSeconds = durationSeconds,
//     timestamp = timestamp
// )

// fun com.example.spybrain.domain.model.BreathingSession.toEntity() = BreathingSessionEntity(
//     id = id,
//     patternName = patternName,
//     durationSeconds = durationSeconds,
//     timestamp = timestamp
// )

/**
 * РџСЂРµРѕР±СЂР°Р·СѓРµС‚ BreathingSessionEntity РІ РґРѕРјРµРЅРЅСѓСЋ РјРѕРґРµР»СЊ.
 * @return Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ BreathingSession.
 */
fun BreathingSessionEntity.toBreathingEntity(): BreathingSession = BreathingSession(
    patternName = patternName,
    durationSeconds = durationSeconds.toInt(),
    completed = true,
    timestamp = timestamp
)

/** РџСЂРµРѕР±СЂР°Р·РѕРІР°РЅРёРµ domain-РјРѕРґРµР»Рё BreathingSession РІ СЃСѓС‰РЅРѕСЃС‚СЊ. */
fun BreathingSession.toEntity(): BreathingSessionEntity = BreathingSessionEntity(
    patternName = this.patternName,
    durationSeconds = this.durationSeconds.toLong(),
    timestamp = this.timestamp
)

/**
 * Преобразует Session (domain) в BreathingSessionEntity для хранения дыхательной сессии.
 */
fun Session.toBreathingEntity(): BreathingSessionEntity = BreathingSessionEntity(
    patternName = this.relatedItemId ?: "", // или другое поле, если нужно
    durationSeconds = this.durationSeconds,
    timestamp = this.endTime.time // или startTime.time, если нужно
)
