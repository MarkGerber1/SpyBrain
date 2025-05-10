package com.example.spybrain.di

import com.example.spybrain.data.repository.BreathingRepositoryImpl
import com.example.spybrain.data.repository.MeditationRepositoryImpl
import com.example.spybrain.data.repository.ProfileRepositoryImpl
import com.example.spybrain.data.repository.SettingsRepositoryImpl
import com.example.spybrain.data.repository.StatsRepositoryImpl
import com.example.spybrain.domain.repository.BreathingRepository
import com.example.spybrain.domain.repository.MeditationRepository
import com.example.spybrain.domain.repository.ProfileRepository
import com.example.spybrain.domain.repository.SettingsRepository
import com.example.spybrain.domain.repository.StatsRepository
import com.example.spybrain.domain.repository.CustomBreathingPatternRepository
import com.example.spybrain.domain.repository.AchievementsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// NOTE: Этот модуль соответствует Clean Architecture — внедряет реализации через интерфейсы domain-слоя.
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindMeditationRepository(impl: MeditationRepositoryImpl): MeditationRepository

    @Binds
    @Singleton
    abstract fun bindBreathingRepository(impl: BreathingRepositoryImpl): BreathingRepository

    @Binds
    @Singleton
    abstract fun bindStatsRepository(impl: StatsRepositoryImpl): StatsRepository

    @Binds
    @Singleton
    abstract fun bindCustomBreathingPatternRepository(
        impl: com.example.spybrain.data.repository.CustomBreathingPatternRepositoryImpl
    ): com.example.spybrain.domain.repository.CustomBreathingPatternRepository

    @Binds
    @Singleton
    abstract fun bindMeditationProgramRepository(
        impl: com.example.spybrain.data.repository.MeditationProgramRepositoryImpl
    ): com.example.spybrain.domain.repository.MeditationProgramRepository

    @Binds
    @Singleton
    abstract fun bindAchievementsRepository(
        impl: com.example.spybrain.data.repository.AchievementsRepositoryImpl
    ): com.example.spybrain.domain.repository.AchievementsRepository
} 