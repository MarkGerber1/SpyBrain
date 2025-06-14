package com.example.spybrain.data.storage.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "heart_rate_measurements")
data class HeartRateMeasurement(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val heartRate: Int,
    val timestamp: LocalDateTime
) 