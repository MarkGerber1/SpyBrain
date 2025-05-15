package com.example.spybrain.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.spybrain.data.model.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    
    @Query("SELECT * FROM reminders")
    fun getAll(): Flow<List<ReminderEntity>>
    
    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ReminderEntity?
    
    @Query("SELECT * FROM reminders WHERE daysOfWeek & (1 << :dayOfWeek) > 0 AND isEnabled = 1")
    suspend fun getByDaysOfWeek(dayOfWeek: Int): List<ReminderEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity)
    
    @Update
    suspend fun update(reminder: ReminderEntity)
    
    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: String)
} 