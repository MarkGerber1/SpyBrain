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

/**
 * Абстрактный класс базы данных для хранения данных Room.
 */
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
    exportSchema = false // FIXME: Установить в true и указать exportSchema = true для экспорта схемы и корректной работы миграций. Необходимо исправить.
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Получить DAO для сессий дыхания.
     * @return BreathingSessionDao.
     */
    abstract fun breathingSessionDao(): BreathingSessionDao
    /**
     * Получить DAO для сессий медитации.
     * @return MeditationSessionDao.
     */
    abstract fun meditationSessionDao(): MeditationSessionDao
    /**
     * Получить DAO для профиля пользователя.
     * @return UserProfileDao.
     */
    abstract fun userProfileDao(): UserProfileDao
    /**
     * Получить DAO для пользовательских дыхательных паттернов.
     * @return CustomBreathingPatternDao.
     */
    abstract fun customBreathingPatternDao(): CustomBreathingPatternDao
    /**
     * Получить DAO для достижений.
     * @return AchievementDao.
     */
    abstract fun achievementDao(): com.example.spybrain.data.storage.dao.AchievementDao
    /**
     * Получить DAO для измерения пульса.
     * @return HeartRateDao.
     */
    abstract fun heartRateDao(): HeartRateDao
}
