package com.example.spybrain.data.storage

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Конвертеры для типов данных Room
 */
class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }
    }
    
    @TypeConverter
    fun toTimestamp(dateTime: LocalDateTime?): Long? {
        return dateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }
} 