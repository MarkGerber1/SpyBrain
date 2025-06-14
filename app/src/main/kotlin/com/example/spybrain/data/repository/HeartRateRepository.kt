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

@Singleton
class HeartRateRepository @Inject constructor(
    private val heartRateDao: HeartRateDao,
    private val settingsDataStore: SettingsDataStore
) {
    
    suspend fun saveMeasurement(heartRate: Int) {
        val measurement = HeartRateMeasurement(
            heartRate = heartRate,
            timestamp = LocalDateTime.now()
        )
        heartRateDao.insertMeasurement(measurement)
    }
    
    suspend fun getMeasurementHistory(): List<Int> {
        return heartRateDao.getAllMeasurements()
            .map { it.heartRate }
            .takeLast(20) // Последние 20 измерений
    }
    
    suspend fun getMotivationalPoints(): Int {
        return settingsDataStore.getMotivationalPoints()
    }
    
    suspend fun addMotivationalPoint(): Int {
        val currentPoints = settingsDataStore.getMotivationalPoints()
        val newPoints = currentPoints + 1
        settingsDataStore.setMotivationalPoints(newPoints)
        return newPoints
    }
    
    fun getMeasurementHistoryFlow(): Flow<List<HeartRateMeasurement>> {
        return heartRateDao.getAllMeasurementsFlow()
    }
    
    suspend fun clearHistory() {
        heartRateDao.deleteAllMeasurements()
    }
    
    suspend fun getAverageHeartRate(): Float {
        val measurements = heartRateDao.getAllMeasurements()
        return if (measurements.isNotEmpty()) {
            measurements.map { it.heartRate }.average().toFloat()
        } else {
            0f
        }
    }
    
    suspend fun getTodayMeasurements(): List<Int> {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        return heartRateDao.getMeasurementsFromDate(startOfDay)
            .filter { it.timestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < endOfDay }
            .map { it.heartRate }
    }
} 