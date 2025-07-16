package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.SessionType
import java.util.Date

/**
 * РЎСѓС‰РЅРѕСЃС‚СЊ СЃРµСЃСЃРёРё РјРµРґРёС‚Р°С†РёРё РґР»СЏ С…СЂР°РЅРµРЅРёСЏ РІ Р±Р°Р·Рµ РґР°РЅРЅС‹С….
 * @property id РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ.
 * @property durationSeconds Р”Р»РёС‚РµР»СЊРЅРѕСЃС‚СЊ СЃРµСЃСЃРёРё РІ СЃРµРєСѓРЅРґР°С….
 * @property timestamp Р’СЂРµРјСЏ СЃРѕР·РґР°РЅРёСЏ (timestamp).
 */
@Entity(tableName = "meditation_sessions")
data class MeditationSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val durationSeconds: Long,
    val timestamp: Long
)

/**
 * РџСЂРµРѕР±СЂР°Р·СѓРµС‚ MeditationSessionEntity РІ РґРѕРјРµРЅРЅСѓСЋ РјРѕРґРµР»СЊ.
 * @return Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ MeditationSession.
 */
fun MeditationSessionEntity.toDomain(): Session = Session(
    id = id.toString(),
    type = SessionType.MEDITATION,
    startTime = Date(timestamp),
    endTime = Date(timestamp + durationSeconds * 1000),
    durationSeconds = durationSeconds,
    relatedItemId = null
)

/**
 * РџСЂРµРѕР±СЂР°Р·СѓРµС‚ MeditationSession РІ MeditationSessionEntity.
 * @return РЎСѓС‰РЅРѕСЃС‚СЊ Р±Р°Р·С‹ РґР°РЅРЅС‹С….
 */
fun Session.toEntity(): MeditationSessionEntity = MeditationSessionEntity(
    id = id.toIntOrNull() ?: 0,
    durationSeconds = durationSeconds,
    timestamp = startTime.time
)
