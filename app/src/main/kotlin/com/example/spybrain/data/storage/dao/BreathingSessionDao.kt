package com.example.spybrain.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spybrain.data.model.BreathingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BreathingSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: BreathingSessionEntity)

    @Query("SELECT * FROM breathing_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<BreathingSessionEntity>>
} 