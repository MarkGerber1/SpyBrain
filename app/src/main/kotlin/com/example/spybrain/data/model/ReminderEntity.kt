package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.Reminder
import com.example.spybrain.domain.model.ReminderType
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val timeStr: String,
    val typeStr: String,
    val daysOfWeek: Int, // Битовая маска дней недели: 1 = Пн, 2 = Вт, 4 = Ср, и т.д.
    val isEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val customActionId: String? = null
)

fun ReminderEntity.toDomain(): Reminder {
    val formatter = DateTimeFormatter.ISO_LOCAL_TIME
    return Reminder(
        id = id,
        title = title,
        description = description,
        time = LocalTime.parse(timeStr, formatter),
        type = ReminderType.valueOf(typeStr),
        daysOfWeek = daysOfWeek,
        isEnabled = isEnabled,
        vibrationEnabled = vibrationEnabled,
        soundEnabled = soundEnabled,
        customActionId = customActionId
    )
}

fun Reminder.toEntity(): ReminderEntity {
    val formatter = DateTimeFormatter.ISO_LOCAL_TIME
    return ReminderEntity(
        id = id,
        title = title,
        description = description,
        timeStr = time.format(formatter),
        typeStr = type.name,
        daysOfWeek = daysOfWeek,
        isEnabled = isEnabled,
        vibrationEnabled = vibrationEnabled,
        soundEnabled = soundEnabled,
        customActionId = customActionId
    )
} 