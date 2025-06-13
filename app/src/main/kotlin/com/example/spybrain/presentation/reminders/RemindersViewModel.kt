package com.example.spybrain.presentation.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spybrain.domain.model.Reminder
import com.example.spybrain.domain.model.Schedule
import com.example.spybrain.domain.model.ScheduleType
import com.example.spybrain.domain.repository.ReminderRepository
import com.example.spybrain.util.WEEKDAYS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject
import timber.log.Timber

/**
 * ViewModel для экрана напоминаний
 */
@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "Ошибка в RemindersViewModel корутине")
    }
    
    // Потоки данных
    val reminders = reminderRepository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    val schedules = reminderRepository.getAllSchedules()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    /**
     * Добавляет новое напоминание
     */
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                reminderRepository.addReminder(reminder)
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при добавлении напоминания")
            }
        }
    }
    
    /**
     * Удаляет напоминание
     */
    fun deleteReminder(id: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                reminderRepository.deleteReminder(id)
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при удалении напоминания")
            }
        }
    }
    
    /**
     * Включает или отключает напоминание
     */
    fun toggleReminderEnabled(id: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                val reminder = reminderRepository.getReminderById(id)
                reminder?.let {
                    reminderRepository.setReminderEnabled(id, !it.isEnabled)
                }
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при переключении состояния напоминания")
            }
        }
    }
    
    /**
     * Добавляет новое расписание
     */
    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                reminderRepository.addSchedule(schedule)
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при добавлении расписания")
            }
        }
    }
    
    /**
     * Удаляет расписание
     */
    fun deleteSchedule(id: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                reminderRepository.deleteSchedule(id)
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при удалении расписания")
            }
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
    
    override fun onCleared() {
        try {
            // Дополнительная очистка ресурсов при необходимости
            super.onCleared()
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при очистке RemindersViewModel")
        }
    }
} 