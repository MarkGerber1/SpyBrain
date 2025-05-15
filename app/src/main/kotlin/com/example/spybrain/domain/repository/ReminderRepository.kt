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
     * Получает список всех расписаний
     */
    fun getAllSchedules(): Flow<List<Schedule>>
    
    /**
     * Добавляет новое напоминание
     * @param reminder Напоминание для добавления
     */
    suspend fun addReminder(reminder: Reminder)
    
    /**
     * Удаляет напоминание по ID
     * @param id ID напоминания
     */
    suspend fun deleteReminder(id: String)
    
    /**
     * Получает напоминание по ID
     * @param id ID напоминания
     * @return Напоминание или null, если не найдено
     */
    suspend fun getReminderById(id: String): Reminder?
    
    /**
     * Включает или отключает напоминание
     * @param id ID напоминания
     * @param enabled Новое состояние
     */
    suspend fun setReminderEnabled(id: String, enabled: Boolean)
    
    /**
     * Добавляет новое расписание
     * @param schedule Расписание для добавления
     */
    suspend fun addSchedule(schedule: Schedule)
    
    /**
     * Удаляет расписание по ID
     * @param id ID расписания
     */
    suspend fun deleteSchedule(id: String)
    
    /**
     * Получает расписание по ID
     * @param id ID расписания
     * @return Расписание или null, если не найдено
     */
    suspend fun getScheduleById(id: String): Schedule?
    
    /**
     * Включает или отключает расписание
     * @param id ID расписания
     * @param enabled Новое состояние
     */
    suspend fun setScheduleEnabled(id: String, enabled: Boolean)
} 