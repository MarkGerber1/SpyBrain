package com.example.spybrain.data.storage.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * РљР»Р°СЃСЃ РёР·РјРµСЂРµРЅРёСЏ РїСѓР»СЊСЃР° РґР»СЏ С…СЂР°РЅРµРЅРёСЏ РІ Р±Р°Р·Рµ РґР°РЅРЅС‹С….
 * @property id РЈРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РёР·РјРµСЂРµРЅРёСЏ.
 * @property heartRate Р—РЅР°С‡РµРЅРёРµ РїСѓР»СЊСЃР° (СѓРґ/РјРёРЅ).
 * @property timestamp Р’СЂРµРјСЏ РёР·РјРµСЂРµРЅРёСЏ (ISO-СЃС‚СЂРѕРєР°).
 */
@Entity(tableName = "heart_rate_measurements")
data class HeartRateMeasurement(
    /** РЈРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РёР·РјРµСЂРµРЅРёСЏ. */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** Р—РЅР°С‡РµРЅРёРµ РїСѓР»СЊСЃР° (СѓРґ/РјРёРЅ). */
    val heartRate: Int,
    /** Р’СЂРµРјСЏ РёР·РјРµСЂРµРЅРёСЏ (ISO-СЃС‚СЂРѕРєР°). */
    val timestamp: LocalDateTime
)
