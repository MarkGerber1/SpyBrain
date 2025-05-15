package com.example.spybrain.data.repository

import com.example.spybrain.domain.model.Reminder
import com.example.spybrain.domain.model.Schedule
import com.example.spybrain.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация репозитория для работы с напоминаниями и расписаниями
 */
@Singleton
class ReminderRepositoryImpl @Inject constructor() : ReminderRepository {
    
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    
    override fun getAllReminders(): Flow<List<Reminder>> = _reminders.asStateFlow()
    
    override fun getAllSchedules(): Flow<List<Schedule>> = _schedules.asStateFlow()
    
    override suspend fun addReminder(reminder: Reminder) {
        _reminders.value = _reminders.value + reminder
    }
    
    override suspend fun deleteReminder(id: String) {
        _reminders.value = _reminders.value.filter { it.id != id }
    }
    
    override suspend fun getReminderById(id: String): Reminder? {
        return _reminders.value.find { it.id == id }
    }
    
    override suspend fun setReminderEnabled(id: String, enabled: Boolean) {
        _reminders.value = _reminders.value.map {
            if (it.id == id) it.copy(isEnabled = enabled) else it
        }
    }
    
    override suspend fun addSchedule(schedule: Schedule) {
        _schedules.value = _schedules.value + schedule
    }
    
    override suspend fun deleteSchedule(id: String) {
        _schedules.value = _schedules.value.filter { it.id != id }
    }
    
    override suspend fun getScheduleById(id: String): Schedule? {
        return _schedules.value.find { it.id == id }
    }
    
    override suspend fun setScheduleEnabled(id: String, enabled: Boolean) {
        _schedules.value = _schedules.value.map {
            if (it.id == id) it.copy(isEnabled = enabled) else it
        }
    }
} 