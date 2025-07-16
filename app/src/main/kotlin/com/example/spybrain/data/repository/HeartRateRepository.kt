package com.example.spybrain.data.repository

import com.example.spybrain.data.datastore.SettingsDataStore
import com.example.spybrain.data.storage.dao.HeartRateDao
import com.example.spybrain.data.storage.model.HeartRateMeasurement
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с измерениями пульса,
  хранением истории и мотивационными баллами пользователя.
 */
@Singleton
class HeartRateRepository @Inject constructor(
    private val heartRateDao: HeartRateDao,
    private val settingsDataStore: SettingsDataStore
) {

    /**
     * Сохранить измерение пульса.
     * @param measurement Измерение пульса.
     */
    suspend fun saveMeasurement(heartRate: Int) {
        val measurement = HeartRateMeasurement(
            heartRate = heartRate,
            timestamp = LocalDateTime.now()
        )
        heartRateDao.insertMeasurement(measurement)
    }

    /**
     * Получить историю измерений пульса.
     * @return Список измерений пульса.
     */
    suspend fun getMeasurementHistory(): List<HeartRateMeasurement> {
        return heartRateDao.getAllMeasurements()
            .takeLast(20)
    }

    /**
     * Получить мотивационные баллы пользователя.
     * @return Количество баллов.
     */
    suspend fun getMotivationalPoints(): Int {
        return settingsDataStore.getMotivationalPoints()
    }

    /**
     * Добавить мотивационный балл.
     */
    suspend fun addMotivationalPoint(): Int {
        val currentPoints = settingsDataStore.getMotivationalPoints()
        val newPoints = currentPoints + 1
        settingsDataStore.setMotivationalPoints(newPoints)
        return newPoints
    }

    /**
     * Получить поток измерений пульса.
     * @return Flow с потоком измерений.
     */
    fun getMeasurementHistoryFlow(): Flow<List<HeartRateMeasurement>> {
        return heartRateDao.getAllMeasurementsFlow()
    }

    /**
     * Очистить историю измерений пульса.
     */
    suspend fun clearHistory() {
        heartRateDao.deleteAllMeasurements()
    }

    /**
     * Получить среднее значение пульса.
     * @return Среднее значение пульса.
     */
    suspend fun getAverageHeartRate(): Float {
        val measurements = heartRateDao.getAllMeasurements()
        return if (measurements.isNotEmpty()) {
            measurements.map { it.heartRate }.average().toFloat()
        } else {
            0f
        }
    }

    /**
     * Получить ежедневные измерения пульса.
     * @return Список измерений за день.
     */
    suspend fun getTodayMeasurements(): List<HeartRateMeasurement> {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return heartRateDao.getMeasurementsFromDate(startOfDay)
            .filter { it.timestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < endOfDay }
    }
}
