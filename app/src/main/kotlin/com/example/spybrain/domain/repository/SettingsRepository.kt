package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Интерфейс репозитория пользовательских настроек.
 */
interface SettingsRepository {
    /**
     * Получить настройки пользователя.
     * @return Настройки пользователя.
     */
    fun getSettings(): Flow<Settings>

    /**
     * Сохранить настройки пользователя.
     * @param settings Новые настройки.
     */
    suspend fun saveSettings(settings: Settings)

    /**
     * Получить имя пользователя.
     * @return Имя пользователя или пустая строка если не установлено.
     */
    suspend fun getUserName(): String

    /**
     * Установить имя пользователя.
     * @param name Имя пользователя.
     */
    suspend fun setUserName(name: String)

    /**
     * Получить дату рождения пользователя.
     * @return Дата рождения или null если не установлена.
     */
    suspend fun getUserBirthDate(): LocalDate?

    /**
     * Установить дату рождения пользователя.
     * @param birthDate Дата рождения.
     */
    suspend fun setUserBirthDate(birthDate: LocalDate)

    /**
     * Проверить, завершил ли пользователь онбординг.
     * @return true если онбординг завершен.
     */
    suspend fun isOnboardingCompleted(): Boolean

    /**
     * Установить статус завершения онбординга.
     * @param completed true если онбординг завершен.
     */
    suspend fun setOnboardingCompleted(completed: Boolean)

    /**
     * Получить статус умного ambient режима.
     * @return true если умный ambient включен.
     */
    suspend fun getSmartAmbientEnabled(): Boolean

    /**
     * Установить статус умного ambient режима.
     * @param enabled true для включения умного ambient.
     */
    suspend fun setSmartAmbientEnabled(enabled: Boolean)

    /**
     * Получить время sleep таймера в минутах.
     * @return Время в минутах или 0 если таймер отключен.
     */
    suspend fun getSleepTimerMinutes(): Int

    /**
     * Установить время sleep таймера.
     * @param minutes Время в минутах (0 для отключения таймера).
     */
    suspend fun setSleepTimerMinutes(minutes: Int)
}
