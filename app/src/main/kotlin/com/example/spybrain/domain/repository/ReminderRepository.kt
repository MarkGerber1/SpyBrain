package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Reminder
import com.example.spybrain.domain.model.Schedule
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Интерфейс репозитория для работы с напоминаниями и расписаниями
 */
interface ReminderRepository {
    
    /**
     * Получает список всех напоминаний
     */
    fun getAllReminders(): Flow<List<Reminder>>
    
    /**
     * Получает напоминание по ID
     * @param id ID напоминания
     */
    suspend fun getReminderById(id: String): Reminder?
    
    /**
     * Получает напоминания, активные в указанную дату
     * @param date Дата для проверки напоминаний
     */
    suspend fun getRemindersForDate(date: LocalDate): List<Reminder>
    
    /**
     * Добавляет новое напоминание
     * @param reminder Напоминание для добавления
     * @return ID созданного напоминания
     */
    suspend fun addReminder(reminder: Reminder): String
    
    /**
     * Обновляет существующее напоминание
     * @param reminder Напоминание с обновленными данными
     */
    suspend fun updateReminder(reminder: Reminder)
    
    /**
     * Удаляет напоминание
     * @param id ID напоминания для удаления
     */
    suspend fun deleteReminder(id: String)
    
    /**
     * Включает или отключает напоминание
     * @param id ID напоминания
     * @param enabled Новое состояние
     */
    suspend fun setReminderEnabled(id: String, enabled: Boolean)
    
    /**
     * Получает список всех расписаний
     */
    fun getAllSchedules(): Flow<List<Schedule>>
    
    /**
     * Получает расписание по ID
     * @param id ID расписания
     */
    suspend fun getScheduleById(id: String): Schedule?
    
    /**
     * Добавляет новое расписание
     * @param schedule Расписание для добавления
     * @return ID созданного расписания
     */
    suspend fun addSchedule(schedule: Schedule): String
    
    /**
     * Обновляет существующее расписание
     * @param schedule Расписание с обновленными данными
     */
    suspend fun updateSchedule(schedule: Schedule)
    
    /**
     * Удаляет расписание
     * @param id ID расписания для удаления
     */
    suspend fun deleteSchedule(id: String)
    
    /**
     * Включает или отключает расписание
     * @param id ID расписания
     * @param enabled Новое состояние
     */
    suspend fun setScheduleEnabled(id: String, enabled: Boolean)
} 