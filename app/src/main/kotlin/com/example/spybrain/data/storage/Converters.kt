package com.example.spybrain.data.storage

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * РљР»Р°СЃСЃ РєРѕРЅРІРµСЂС‚РµСЂРѕРІ РґР»СЏ РїСЂРµРѕР±СЂР°Р·РѕРІР°РЅРёСЏ С‚РёРїРѕРІ РґР°РЅРЅС‹С… РІ Room (РЅР°РїСЂРёРјРµСЂ, LocalDateTime <-> Long).
 */
class Converters {
    /**
     * РџСЂРµРѕР±СЂР°Р·РѕРІР°С‚СЊ timestamp РІ LocalDateTime.
     * @param value Timestamp.
     * @return LocalDateTime РёР»Рё null.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }
    }
    /**
     * РџСЂРµРѕР±СЂР°Р·РѕРІР°С‚СЊ LocalDateTime РІ timestamp.
     * @param dateTime LocalDateTime.
     * @return Timestamp РёР»Рё null.
     */
    @TypeConverter
    fun toTimestamp(dateTime: LocalDateTime?): Long? {
        return dateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }
}
