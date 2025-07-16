package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.Reminder
import com.example.spybrain.domain.model.Schedule
import com.example.spybrain.domain.model.ScheduleType
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * РЎСѓС‰РЅРѕСЃС‚СЊ РґР»СЏ С…СЂР°РЅРµРЅРёСЏ СЂР°СЃРїРёСЃР°РЅРёР№.
 * @property id РЈРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ.
 * @property title РќР°Р·РІР°РЅРёРµ СЂР°СЃРїРёСЃР°РЅРёСЏ.
 * @property description РћРїРёСЃР°РЅРёРµ СЂР°СЃРїРёСЃР°РЅРёСЏ.
 * @property startTimeStr Р’СЂРµРјСЏ РЅР°С‡Р°Р»Р° (СЃС‚СЂРѕРєР°).
 * @property endTimeStr Р’СЂРµРјСЏ РѕРєРѕРЅС‡Р°РЅРёСЏ (СЃС‚СЂРѕРєР°).
 * @property typeStr РўРёРї СЂР°СЃРїРёСЃР°РЅРёСЏ (СЃС‚СЂРѕРєР°).
 * @property daysOfWeek Р”РЅРё РЅРµРґРµР»Рё (Р±РёС‚РѕРІР°СЏ РјР°СЃРєР°).
 * @property isEnabled Р’РєР»СЋС‡РµРЅРѕ Р»Рё СЂР°СЃРїРёСЃР°РЅРёРµ.
 * @property notificationEnabled Р’РєР»СЋС‡РµРЅС‹ Р»Рё СѓРІРµРґРѕРјР»РµРЅРёСЏ.
 * @property itemId РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ СЃРІСЏР·Р°РЅРЅРѕРіРѕ СЌР»РµРјРµРЅС‚Р°.
 * @property reminders РЎРїРёСЃРѕРє РЅР°РїРѕРјРёРЅР°РЅРёР№ РґР»СЏ СЂР°СЃРїРёСЃР°РЅРёСЏ.
 */
@Entity(tableName = "schedules")
data class ScheduleEntity(
    /** РЈРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ. */
    @PrimaryKey val id: String,
    /** РќР°Р·РІР°РЅРёРµ СЂР°СЃРїРёСЃР°РЅРёСЏ. */
    val title: String,
    /** РћРїРёСЃР°РЅРёРµ СЂР°СЃРїРёСЃР°РЅРёСЏ. */
    val description: String? = null,
    /** Р’СЂРµРјСЏ РЅР°С‡Р°Р»Р° (РјР»РЅСЋСЋ СЃРµРєСѓРЅРґ). */
    val startTime: Long,
    /** Р’СЂРµРјСЏ РѕРєРѕРЅС‡Р°РЅРёСЏ (РјР»РЅСЋСЋ СЃРµРєСѓРЅРґ). */
    val endTime: Long,
    /** РўРёРї СЂР°СЃРїРёСЃР°РЅРёСЏ (СЃС‚СЂРѕРєР°). */
    val type: String,
    /** Р”РЅРё РЅРµРґРµР»Рё (Р±РёС‚РѕРІР°СЏ РјР°СЃРєР°). */
    val daysOfWeek: String, // сериализованный список (например, "1,2,3")
    /** Р’РєР»СЋС‡РµРЅРѕ Р»Рё СЂР°СЃРїРёСЃР°РЅРёРµ. */
    val isEnabled: Boolean = true,
    /** Р’РєР»СЋС‡РµРЅС‹ Р»Рё СѓРІРµРґРѕРјР»РµРЅРёСЏ. */
    val notificationEnabled: Boolean = true,
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ СЃРІСЏР·Р°РЅРЅРѕРіРѕ СЌР»РµРјРµРЅС‚Р°. */
    val itemId: String? = null // ID СЃРІСЏР·Р°РЅРЅРѕРіРѕ СЌР»РµРјРµРЅС‚Р° (РјРµРґРёС‚Р°С†РёРё, РґС‹С…Р°С‚РµР»СЊРЅРѕРіРѕ СѓРїСЂР°Р¶РЅРµРЅРёСЏ)
) {
    /** РЎРїРёСЃРѕРє РЅР°РїРѕРјРёРЅР°РЅРёР№ РґР»СЏ СЂР°СЃРїРёСЃР°РЅРёСЏ. */
    @Ignore
    var reminders: List<Reminder> = emptyList()

    /**
     * РџСЂРµРѕР±СЂР°Р·СѓРµС‚ ScheduleEntity РІ РґРѕРјРµРЅРЅСѓСЋ РјРѕРґРµР»СЊ.
     * @return Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ Schedule.
     */
    fun toDomain(): Schedule {
        return Schedule(
            id = id,
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            type = ScheduleType.valueOf(type),
            daysOfWeek = daysOfWeek.split(",").mapNotNull { it.toIntOrNull() },
            isEnabled = isEnabled,
            notificationEnabled = notificationEnabled,
            itemId = itemId,
            reminders = reminders
        )
    }

    companion object {
        /**
         * Создание из доменной модели.
         */
        fun fromDomain(schedule: Schedule): ScheduleEntity {
            return ScheduleEntity(
                id = schedule.id,
                title = schedule.title,
                description = schedule.description,
                startTime = schedule.startTime,
                endTime = schedule.endTime,
                type = schedule.type.name,
                daysOfWeek = schedule.daysOfWeek.joinToString(","),
                isEnabled = schedule.isEnabled,
                notificationEnabled = schedule.notificationEnabled,
                itemId = schedule.itemId
            ).apply {
                reminders = schedule.reminders
            }
        }
    }
}

/**
 * РџСЂРµРѕР±СЂР°Р·СѓРµС‚ Schedule РІ ScheduleEntity.
 * @return РЎСѓС‰РЅРѕСЃС‚СЊ Р±Р°Р·С‹ РґР°РЅРЅС‹С….
 */
fun Schedule.toEntity(): ScheduleEntity = ScheduleEntity.fromDomain(this)
