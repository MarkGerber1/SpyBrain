package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.SessionType
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.BreathingSession
import java.time.ZoneId
import java.time.Instant

@Entity(tableName = "breathing_sessions")
data class BreathingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patternName: String,
    val durationSeconds: Long,
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

// Преобразование domain-модели Session в сущность BreathingSessionEntity для хранения в базе данных
fun Session.toBreathingEntity(): BreathingSessionEntity = BreathingSessionEntity(
    patternName = this.relatedItemId ?: "",
    durationSeconds = this.durationSeconds,
    timestamp = this.startTime.time
)

/** Преобразование domain-модели BreathingSession в сущность */
fun BreathingSession.toEntity(): BreathingSessionEntity = BreathingSessionEntity(
    patternName = this.patternName,
    durationSeconds = this.durationSeconds.toLong(),
    timestamp = this.timestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
) 