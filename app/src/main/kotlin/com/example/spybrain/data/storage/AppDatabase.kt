package com.example.spybrain.data.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.spybrain.data.model.BreathingSessionEntity
import com.example.spybrain.data.model.MeditationSessionEntity
import com.example.spybrain.data.model.UserProfileEntity
import com.example.spybrain.data.model.CustomBreathingPatternEntity
import com.example.spybrain.data.model.AchievementEntity
import com.example.spybrain.data.storage.model.HeartRateMeasurement
import com.example.spybrain.data.storage.dao.BreathingSessionDao
import com.example.spybrain.data.storage.dao.MeditationSessionDao
import com.example.spybrain.data.storage.dao.UserProfileDao
import com.example.spybrain.data.storage.dao.CustomBreathingPatternDao
import com.example.spybrain.data.storage.dao.HeartRateDao

@Database(
    entities = [
        BreathingSessionEntity::class, 
        MeditationSessionEntity::class, 
        UserProfileEntity::class, 
        CustomBreathingPatternEntity::class, 
        AchievementEntity::class,
        HeartRateMeasurement::class
    ],
    version = 4,
    exportSchema = false // FIXME: Установить в true и указать exportSchema = true для экспорта схемы и корректной работы миграций. Необходимо проверить и протестировать миграции для всех изменений схемы между версиями 1, 2 и 3.
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun breathingSessionDao(): BreathingSessionDao
    abstract fun meditationSessionDao(): MeditationSessionDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun customBreathingPatternDao(): CustomBreathingPatternDao
    /** DAO для достижений */
    abstract fun achievementDao(): com.example.spybrain.data.storage.dao.AchievementDao
    abstract fun heartRateDao(): HeartRateDao
} 