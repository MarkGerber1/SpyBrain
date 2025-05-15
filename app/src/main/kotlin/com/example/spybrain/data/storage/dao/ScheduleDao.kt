package com.example.spybrain.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.spybrain.data.model.ScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    
    @Query("SELECT * FROM schedules")
    fun getAll(): Flow<List<ScheduleEntity>>
    
    @Query("SELECT * FROM schedules WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ScheduleEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleEntity)
    
    @Update
    suspend fun update(schedule: ScheduleEntity)
    
    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteById(id: String)
} 