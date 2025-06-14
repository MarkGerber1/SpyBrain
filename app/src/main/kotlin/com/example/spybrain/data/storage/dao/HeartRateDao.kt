package com.example.spybrain.data.storage.dao

import androidx.room.*
import com.example.spybrain.data.storage.model.HeartRateMeasurement
import kotlinx.coroutines.flow.Flow

@Dao
interface HeartRateDao {
    
    @Query("SELECT * FROM heart_rate_measurements ORDER BY timestamp DESC")
    suspend fun getAllMeasurements(): List<HeartRateMeasurement>
    
    @Query("SELECT * FROM heart_rate_measurements ORDER BY timestamp DESC")
    fun getAllMeasurementsFlow(): Flow<List<HeartRateMeasurement>>
    
    @Query("SELECT * FROM heart_rate_measurements WHERE timestamp >= :startDate ORDER BY timestamp DESC")
    suspend fun getMeasurementsFromDate(startDate: Long): List<HeartRateMeasurement>
    
    @Query("SELECT AVG(heartRate) FROM heart_rate_measurements WHERE timestamp >= :startOfDay AND timestamp < :endOfDay")
    suspend fun getAverageHeartRateForDate(startOfDay: Long, endOfDay: Long): Float?
    
    @Insert
    suspend fun insertMeasurement(measurement: HeartRateMeasurement)
    
    @Delete
    suspend fun deleteMeasurement(measurement: HeartRateMeasurement)
    
    @Query("DELETE FROM heart_rate_measurements")
    suspend fun deleteAllMeasurements()
    
    @Query("DELETE FROM heart_rate_measurements WHERE timestamp < :date")
    suspend fun deleteOldMeasurements(date: Long)
} 