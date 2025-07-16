package com.example.spybrain.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import androidx.room.Room
import android.content.Context
import dagger.Provides
import com.example.spybrain.data.storage.AppDatabase
import com.example.spybrain.data.storage.dao.BreathingSessionDao
import com.example.spybrain.data.storage.dao.MeditationSessionDao
import com.example.spybrain.data.storage.dao.UserProfileDao
import com.example.spybrain.data.storage.dao.CustomBreathingPatternDao
import com.example.spybrain.data.storage.dao.AchievementDao
import com.example.spybrain.data.storage.dao.HeartRateDao
import com.example.spybrain.data.storage.MIGRATION_1_2
import com.example.spybrain.data.storage.MIGRATION_2_3
import com.example.spybrain.data.storage.MIGRATION_3_4

/**
 * DI-модуль для представления зависимостей хранения.
 */
@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    /**
     * Предоставляет AppDatabase.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "spybrain.db"
        )
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
        .build()

    /**
     * Предоставляет BreathingSessionDao.
     */
    @Provides
    fun provideBreathingSessionDao(db: AppDatabase): BreathingSessionDao =
        db.breathingSessionDao() // FIXME: Не внедрять DAO напрямую в UseCase/ViewModel. Использовать репозиторий.

    /**
     * Предоставляет UserProfileDao.
     */
    @Provides
    fun provideUserProfileDao(db: AppDatabase): UserProfileDao =
        db.userProfileDao() // FIXME: Не внедрять DAO напрямую в UseCase/ViewModel. Использовать репозиторий.

    /**
     * Предоставляет MeditationSessionDao.
     */
    @Provides
    fun provideMeditationSessionDao(db: AppDatabase): MeditationSessionDao =
        db.meditationSessionDao() // FIXME: Не внедрять DAO напрямую в UseCase/ViewModel. Использовать репозиторий.

    /**
     * Предоставляет CustomBreathingPatternDao.
     */
    @Provides
    fun provideCustomBreathingPatternDao(db: AppDatabase): CustomBreathingPatternDao =
        db.customBreathingPatternDao() 
        // FIXME: Не внедрять DAO напрямую в UseCase/ViewModel. Использовать репозиторий.

    /**
     * Предоставляет AchievementDao.
     */
    @Provides
    fun provideAchievementDao(db: AppDatabase): AchievementDao =
        db.achievementDao() // FIXME: Не внедрять DAO напрямую в UseCase/ViewModel. Использовать репозиторий.

    /**
     * Предоставляет HeartRateDao.
     */
    @Provides
    fun provideHeartRateDao(db: AppDatabase): HeartRateDao =
        db.heartRateDao()

    /**
     * Предоставляет SettingsDataStore.
     */
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): com.example.spybrain.data.datastore.SettingsDataStore =
        com.example.spybrain.data.datastore.SettingsDataStore(context)

    // Реализация биндинга синглтонов хранения
}
