package com.example.spybrain.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.spybrain.data.model.CustomBreathingPatternEntity

@Dao
interface CustomBreathingPatternDao {
    @Query("SELECT * FROM custom_breathing_patterns")
    fun getAllPatterns(): Flow<List<CustomBreathingPatternEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPattern(pattern: CustomBreathingPatternEntity)

    @Delete
    suspend fun deletePattern(pattern: CustomBreathingPatternEntity)
} 