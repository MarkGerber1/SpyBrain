package com.example.spybrain.domain.model

import java.time.LocalTime

data class Reminder(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val time: LocalTime = LocalTime.NOON,
    val type: ReminderType = ReminderType.ONCE,
    val daysOfWeek: Int = 0,
    val isEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val customActionId: String? = null
) 