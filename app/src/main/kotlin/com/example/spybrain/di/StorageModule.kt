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

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
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

    @Provides
    fun provideBreathingSessionDao(db: AppDatabase): BreathingSessionDao =
        db.breathingSessionDao() // FIXME: Не внедрять DAO напрямую в UseCase/ViewModel. Использовать репозиторий.

    @Provides
    fun provideUserProfileDao(db: AppDatabase): UserProfileDao =
        db.userProfileDao() // FIXME: Не внедрять DAO напрямую в UseCase/ViewModel. Использовать репозиторий.

    @Provides
    fun provideMeditationSessionDao(db: AppDatabase): MeditationSessionDao =
        db.meditationSessionDao() // FIXME: Не внедрять DAO напрямую в UseCase/ViewModel. Использовать репозиторий.

    @Provides
    fun provideCustomBreathingPatternDao(db: AppDatabase): CustomBreathingPatternDao =
        db.customBreathingPatternDao() // FIXME: Не внедрять DAO напрямую в UseCase/ViewModel. Использовать репозиторий.

    @Provides
    fun provideAchievementDao(db: AppDatabase): AchievementDao =
        db.achievementDao() // FIXME: Не внедрять DAO напрямую в UseCase/ViewModel. Использовать репозиторий.

    @Provides
    fun provideHeartRateDao(db: AppDatabase): HeartRateDao =
        db.heartRateDao()

    // Provider for DataStore настроек
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): com.example.spybrain.data.datastore.SettingsDataStore =
        com.example.spybrain.data.datastore.SettingsDataStore(context)

    // Реализация биндингов хранилищ
} 