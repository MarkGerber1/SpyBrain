package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.model.SessionType
import java.util.Date

@Entity(tableName = "meditation_sessions")
 data class MeditationSessionEntity(
     @PrimaryKey(autoGenerate = true) val id: Int = 0,
     val durationSeconds: Long,
     val timestamp: Long
 )

 fun MeditationSessionEntity.toDomain(): Session = Session(
     id = id.toString(),
     type = SessionType.MEDITATION,
     startTime = Date(timestamp),
     endTime = Date(timestamp + durationSeconds * 1000),
     durationSeconds = durationSeconds,
     relatedItemId = null
 )

 fun Session.toEntity(): MeditationSessionEntity = MeditationSessionEntity(
     id = id.toIntOrNull() ?: 0,
     durationSeconds = durationSeconds,
     timestamp = startTime.time
 ) 