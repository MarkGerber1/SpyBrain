package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.Reminder
import com.example.spybrain.domain.model.Schedule
import com.example.spybrain.domain.model.ScheduleType
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val startTimeStr: String,
    val endTimeStr: String,
    val typeStr: String,
    val daysOfWeek: Int, // Битовая маска дней недели: 1 = Пн, 2 = Вт, 4 = Ср, и т.д.
    val isEnabled: Boolean = true,
    val notificationEnabled: Boolean = true,
    val itemId: String? = null // ID связанного элемента (медитации, дыхательного упражнения)
) {
    @Ignore
    var reminders: List<Reminder> = emptyList()
    
    /**
     * Преобразование в доменную модель
     */
    fun toDomain(): Schedule {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return Schedule(
            id = id,
            title = title,
            description = description,
            startTime = LocalTime.parse(startTimeStr, formatter),
            endTime = LocalTime.parse(endTimeStr, formatter),
            type = ScheduleType.valueOf(typeStr),
            daysOfWeek = daysOfWeek,
            isEnabled = isEnabled,
            notificationEnabled = notificationEnabled,
            itemId = itemId,
            reminders = reminders
        )
    }
    
    companion object {
        /**
         * Создание из доменной модели
         */
        fun fromDomain(schedule: Schedule): ScheduleEntity {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            return ScheduleEntity(
                id = schedule.id,
                title = schedule.title,
                description = schedule.description,
                startTimeStr = schedule.startTime.format(formatter),
                endTimeStr = schedule.endTime.format(formatter),
                typeStr = schedule.type.name,
                daysOfWeek = schedule.daysOfWeek,
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
 * Функция расширения для преобразования Schedule в ScheduleEntity
 */
fun Schedule.toEntity(): ScheduleEntity {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return ScheduleEntity(
        id = id,
        title = title,
        description = description,
        startTimeStr = startTime.format(formatter),
        endTimeStr = endTime.format(formatter),
        typeStr = type.name,
        daysOfWeek = daysOfWeek,
        isEnabled = isEnabled,
        notificationEnabled = notificationEnabled,
        itemId = itemId
    ).apply {
        reminders = this@toEntity.reminders
    }
} 