package com.example.spybrain.presentation.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spybrain.domain.model.Reminder
import com.example.spybrain.domain.model.Schedule
import com.example.spybrain.domain.model.ScheduleType
import com.example.spybrain.domain.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel для экрана напоминаний
 */
@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    
    // Потоки данных
    val reminders = reminderRepository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    val schedules = reminderRepository.getAllSchedules()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    companion object {
        val WEEKDAYS = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    }
    
    /**
     * Добавляет новое напоминание
     */
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.addReminder(reminder)
        }
    }
    
    /**
     * Удаляет напоминание
     */
    fun deleteReminder(id: String) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(id)
        }
    }
    
    /**
     * Включает или отключает напоминание
     */
    fun toggleReminderEnabled(id: String) {
        viewModelScope.launch {
            val reminder = reminderRepository.getReminderById(id)
            reminder?.let {
                reminderRepository.setReminderEnabled(id, !it.isEnabled)
            }
        }
    }
    
    /**
     * Добавляет новое расписание
     */
    fun addSchedule(title: String, description: String) {
        viewModelScope.launch {
            val schedule = Schedule(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                startTime = LocalTime.of(8, 0),
                endTime = LocalTime.of(9, 0),
                type = ScheduleType.CUSTOM,
                daysOfWeek = Schedule.WEEKDAYS
            )
            reminderRepository.addSchedule(schedule)
        }
    }
    
    /**
     * Удаляет расписание
     */
    fun deleteSchedule(id: String) {
        viewModelScope.launch {
            reminderRepository.deleteSchedule(id)
        }
    }
    
    /**
     * Включает или отключает расписание
     */
    fun toggleScheduleEnabled(id: String) {
        viewModelScope.launch {
            val schedule = reminderRepository.getScheduleById(id)
            schedule?.let {
                reminderRepository.setScheduleEnabled(id, !it.isEnabled)
            }
        }
    }
} 