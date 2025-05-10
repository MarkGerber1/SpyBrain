package com.example.spybrain.di

import com.example.spybrain.domain.repository.BreathingRepository
import com.example.spybrain.domain.repository.MeditationRepository
import com.example.spybrain.domain.repository.ProfileRepository
import com.example.spybrain.domain.repository.SettingsRepository
import com.example.spybrain.domain.repository.StatsRepository
import com.example.spybrain.domain.usecase.breathing.GetBreathingPatternsUseCase
import com.example.spybrain.domain.usecase.breathing.TrackBreathingSessionUseCase
import com.example.spybrain.domain.usecase.meditation.GetMeditationsUseCase
import com.example.spybrain.domain.usecase.meditation.TrackMeditationSessionUseCase
import com.example.spybrain.domain.usecase.profile.GetUserProfileUseCase
import com.example.spybrain.domain.usecase.profile.SaveUserProfileUseCase
import com.example.spybrain.domain.usecase.settings.GetSettingsUseCase
import com.example.spybrain.domain.usecase.settings.SaveSettingsUseCase
import com.example.spybrain.domain.usecase.stats.GetOverallStatsUseCase
import com.example.spybrain.domain.usecase.stats.GetSessionHistoryUseCase
import com.example.spybrain.domain.usecase.stats.SaveSessionUseCase
import com.example.spybrain.domain.usecase.stats.GetBreathingHistoryUseCase
import com.example.spybrain.domain.usecase.stats.SaveBreathingSessionUseCase
import com.example.spybrain.domain.usecase.breathing.GetCustomBreathingPatternsUseCase
import com.example.spybrain.domain.usecase.breathing.AddCustomBreathingPatternUseCase
import com.example.spybrain.domain.usecase.breathing.DeleteCustomBreathingPatternUseCase
import com.example.spybrain.domain.usecase.meditation.GetMeditationProgramsUseCase
import com.example.spybrain.domain.usecase.achievements.GetAchievementsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // Settings Use Cases (Already existed - assumed from context)
    @Provides
    @Singleton
    fun provideGetSettingsUseCase(repository: SettingsRepository): GetSettingsUseCase {
        return GetSettingsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveSettingsUseCase(repository: SettingsRepository): SaveSettingsUseCase {
        return SaveSettingsUseCase(repository)
    }

    // Profile Use Cases
    @Provides
    @Singleton
    fun provideGetUserProfileUseCase(repository: ProfileRepository): GetUserProfileUseCase {
        return GetUserProfileUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveUserProfileUseCase(repository: ProfileRepository): SaveUserProfileUseCase {
        return SaveUserProfileUseCase(repository)
    }

    // Meditation Use Cases
    @Provides
    @Singleton
    fun provideGetMeditationsUseCase(repository: MeditationRepository): GetMeditationsUseCase {
        return GetMeditationsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideTrackMeditationSessionUseCase(repository: MeditationRepository): TrackMeditationSessionUseCase {
        return TrackMeditationSessionUseCase(repository)
    }

    // Breathing Use Cases
    @Provides
    @Singleton
    fun provideGetBreathingPatternsUseCase(repository: BreathingRepository): GetBreathingPatternsUseCase {
        return GetBreathingPatternsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetCustomBreathingPatternsUseCase(repository: com.example.spybrain.domain.repository.CustomBreathingPatternRepository): GetCustomBreathingPatternsUseCase {
        return GetCustomBreathingPatternsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideTrackBreathingSessionUseCase(repository: BreathingRepository): TrackBreathingSessionUseCase {
        return TrackBreathingSessionUseCase(repository)
    }

    // Stats Use Cases
    @Provides
    @Singleton
    fun provideGetOverallStatsUseCase(repository: StatsRepository): GetOverallStatsUseCase {
        return GetOverallStatsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetSessionHistoryUseCase(repository: StatsRepository): GetSessionHistoryUseCase {
        return GetSessionHistoryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveSessionUseCase(repository: StatsRepository): SaveSessionUseCase {
        return SaveSessionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetBreathingHistoryUseCase(repository: StatsRepository): GetBreathingHistoryUseCase {
        return GetBreathingHistoryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveBreathingSessionUseCase(repository: StatsRepository): SaveBreathingSessionUseCase {
        return SaveBreathingSessionUseCase(repository)
    }

    // Custom Breathing Pattern Use Cases
    @Provides
    @Singleton
    fun provideAddCustomBreathingPatternUseCase(repository: com.example.spybrain.domain.repository.CustomBreathingPatternRepository): com.example.spybrain.domain.usecase.breathing.AddCustomBreathingPatternUseCase {
        return AddCustomBreathingPatternUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteCustomBreathingPatternUseCase(repository: com.example.spybrain.domain.repository.CustomBreathingPatternRepository): com.example.spybrain.domain.usecase.breathing.DeleteCustomBreathingPatternUseCase {
        return DeleteCustomBreathingPatternUseCase(repository)
    }

    // Медитационные программы Use Case
    @Provides
    @Singleton
    fun provideGetMeditationProgramsUseCase(
        repository: com.example.spybrain.domain.repository.MeditationProgramRepository
    ): com.example.spybrain.domain.usecase.meditation.GetMeditationProgramsUseCase {
        return com.example.spybrain.domain.usecase.meditation.GetMeditationProgramsUseCase(repository)
    }

    // Achievements Use Case
    @Provides
    @Singleton
    fun provideGetAchievementsUseCase(
        repository: com.example.spybrain.domain.repository.AchievementsRepository
    ): com.example.spybrain.domain.usecase.achievements.GetAchievementsUseCase {
        return com.example.spybrain.domain.usecase.achievements.GetAchievementsUseCase(repository)
    }
} 