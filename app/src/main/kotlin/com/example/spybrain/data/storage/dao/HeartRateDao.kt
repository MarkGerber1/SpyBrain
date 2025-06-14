package com.example.spybrain.data.storage.dao

import androidx.room.*
import com.example.spybrain.data.storage.model.HeartRateMeasurement
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface HeartRateDao {
    
    @Query("SELECT * FROM heart_rate_measurements ORDER BY timestamp DESC")
    suspend fun getAllMeasurements(): List<HeartRateMeasurement>
    
    @Query("SELECT * FROM heart_rate_measurements ORDER BY timestamp DESC")
    fun getAllMeasurementsFlow(): Flow<List<HeartRateMeasurement>>
    
    @Query("SELECT * FROM heart_rate_measurements WHERE DATE(timestamp) = DATE(:date) ORDER BY timestamp DESC")
    suspend fun getMeasurementsByDate(date: LocalDate): List<HeartRateMeasurement>
    
    @Query("SELECT * FROM heart_rate_measurements WHERE timestamp >= :startDate ORDER BY timestamp DESC")
    suspend fun getMeasurementsFromDate(startDate: LocalDateTime): List<HeartRateMeasurement>
    
    @Query("SELECT AVG(heartRate) FROM heart_rate_measurements WHERE DATE(timestamp) = DATE(:date)")
    suspend fun getAverageHeartRateForDate(date: LocalDate): Float?
    
    @Insert
    suspend fun insertMeasurement(measurement: HeartRateMeasurement)
    
    @Delete
    suspend fun deleteMeasurement(measurement: HeartRateMeasurement)
    
    @Query("DELETE FROM heart_rate_measurements")
    suspend fun deleteAllMeasurements()
    
    @Query("DELETE FROM heart_rate_measurements WHERE timestamp < :date")
    suspend fun deleteOldMeasurements(date: LocalDateTime)
} 