package com.example.spybrain.domain.model

import java.time.LocalTime

data class Schedule(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val startTime: LocalTime = LocalTime.of(9, 0),
    val endTime: LocalTime = LocalTime.of(10, 0),
    val type: ScheduleType = ScheduleType.DAILY,
    val daysOfWeek: Int = 0,
    val isEnabled: Boolean = true,
    val notificationEnabled: Boolean = true,
    val itemId: String? = null,
    val reminders: List<Reminder> = emptyList()
) 