package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.Reminder
import com.example.spybrain.domain.model.ReminderType
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * РЎСѓС‰РЅРѕСЃС‚СЊ РґР»СЏ С…СЂР°РЅРµРЅРёСЏ РЅР°РїРѕРјРёРЅР°РЅРёР№.
 * @property id РЈРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ.
 * @property title РќР°Р·РІР°РЅРёРµ РЅР°РїРѕРјРёРЅР°РЅРёСЏ.
 * @property description РћРїРёСЃР°РЅРёРµ РЅР°РїРѕРјРёРЅР°РЅРёСЏ.
 * @property time Р’СЂРµРјСЏ РЅР°РїРѕРјРёРЅР°РЅРёСЏ (РјРµР¶РґСѓРІРµСЂСЊРµРЅРЅРѕРµ РїРѕР»Рµ).
 * @property type РўРёРї РЅР°РїРѕРјРёРЅР°РЅРёСЏ (СЃС‚СЂРѕРєР°).
 * @property daysOfWeek Р”РЅРё РЅРµРґРµР»Рё (Р±РёС‚РѕРІР°СЏ РјР°СЃРєР°).
 * @property isEnabled Р’РєР»СЋС‡РµРЅРѕ Р»Рё РЅР°РїРѕРјРёРЅР°РЅРёРµ.
 * @property vibrationEnabled Р’РєР»СЋС‡РµРЅР° Р»Рё РІРёР±СЂР°С†РёСЏ.
 * @property soundEnabled Р’РєР»СЋС‡С‘РЅ Р»Рё Р·РІСѓРє.
 * @property customActionId РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РєР°СЃС‚РѕРјРЅРѕРіРѕ РґРµР№СЃС‚РІРёСЏ.
 */
@Entity(tableName = "reminders")
data class ReminderEntity(
    /** РЈРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ. */
    @PrimaryKey val id: String,
    /** РќР°Р·РІР°РЅРёРµ РЅР°РїРѕРјРёРЅР°РЅРёСЏ. */
    val title: String,
    /** РћРїРёСЃР°РЅРёРµ РЅР°РїРѕРјРёРЅР°РЅРёСЏ. */
    val description: String? = null,
    /** Р’СЂРµРјСЏ РЅР°РїРѕРјРёРЅР°РЅРёСЏ (РјРµР¶РґСѓРІРµСЂСЊРµРЅРЅРѕРµ РїРѕР»Рµ). */
    val time: Long,
    /** РўРёРї РЅР°РїРѕРјРёРЅР°РЅРёСЏ (СЃС‚СЂРѕРєР°). */
    val type: String,
    /** Р”РЅРё РЅРµРґРµР»Рё (Р±РёС‚РѕРІР°СЏ РјР°СЃРєР°). */
    val daysOfWeek: String, // сериализованный список (например, "1,2,3")
    /** Р’РєР»СЋС‡РµРЅРѕ Р»Рё РЅР°РїРѕРјРёРЅР°РЅРёРµ. */
    val isEnabled: Boolean = true,
    /** Р’РєР»СЋС‡РµРЅР° Р»Рё РІРёР±СЂР°С†РёСЏ. */
    val vibrationEnabled: Boolean = true,
    /** Р’РєР»СЋС‡С‘РЅ Р»Рё Р·РІСѓРє. */
    val soundEnabled: Boolean = true,
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РєР°СЃС‚РѕРјРЅРѕРіРѕ РґРµР№СЃС‚РІРёСЏ. */
    val customActionId: String? = null
)

/**
 * РџСЂРµРѕР±СЂР°Р·СѓРµС‚ ReminderEntity РІ РґРѕРјРµРЅРЅСѓСЋ РјРѕРґРµР»СЊ.
 * @return Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ Reminder.
 */
fun ReminderEntity.toDomain(): Reminder {
    return Reminder(
        id = id,
        title = title,
        description = description,
        time = time,
        type = ReminderType.valueOf(type),
        daysOfWeek = daysOfWeek.split(",").mapNotNull { it.toIntOrNull() },
        isEnabled = isEnabled,
        vibrationEnabled = vibrationEnabled,
        soundEnabled = soundEnabled,
        customActionId = customActionId
    )
}

/**
 * РџСЂРµРѕР±СЂР°Р·СѓРµС‚ Reminder РІ ReminderEntity.
 * @return РЎСѓС‰РЅРѕСЃС‚СЊ Р±Р°Р·С‹ РґР°РЅРЅС‹С….
 */
fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        title = title,
        description = description,
        time = time,
        type = type.name,
        daysOfWeek = daysOfWeek.joinToString(","),
        isEnabled = isEnabled,
        vibrationEnabled = vibrationEnabled,
        soundEnabled = soundEnabled,
        customActionId = customActionId
    )
}
