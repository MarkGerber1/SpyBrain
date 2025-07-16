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

/**
 * РњРѕРґСѓР»СЊ Dagger РґР»СЏ РїСЂРµРґРѕСЃС‚Р°РІР»РµРЅРёСЏ use case РїСЂРёР»РѕР¶РµРЅРёСЏ.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РЅР°СЃС‚СЂРѕРµРє.
     * @return Р­РєР·РµРјРїР»СЏСЂ GetSettingsUseCase.
     */
    @Provides
    @Singleton
    fun provideGetSettingsUseCase(repository: SettingsRepository): GetSettingsUseCase {
        return GetSettingsUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ СЃРѕС…СЂР°РЅРµРЅРёСЏ РЅР°СЃС‚СЂРѕРµРє.
     * @return Р­РєР·РµРјРїР»СЏСЂ SaveSettingsUseCase.
     */
    @Provides
    @Singleton
    fun provideSaveSettingsUseCase(repository: SettingsRepository): SaveSettingsUseCase {
        return SaveSettingsUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РїСЂРѕС„РёР»СЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return Р­РєР·РµРјРїР»СЏСЂ GetUserProfileUseCase.
     */
    @Provides
    @Singleton
    fun provideGetUserProfileUseCase(repository: ProfileRepository): GetUserProfileUseCase {
        return GetUserProfileUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ СЃРѕС…СЂР°РЅРµРЅРёСЏ РїСЂРѕС„РёР»СЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return Р­РєР·РµРјРїР»СЏСЂ SaveUserProfileUseCase.
     */
    @Provides
    @Singleton
    fun provideSaveUserProfileUseCase(repository: ProfileRepository): SaveUserProfileUseCase {
        return SaveUserProfileUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РјРµРґРёС‚Р°С†РёР№.
     * @return Р­РєР·РµРјРїР»СЏСЂ GetMeditationsUseCase.
     */
    @Provides
    @Singleton
    fun provideGetMeditationsUseCase(repository: MeditationRepository): GetMeditationsUseCase {
        return GetMeditationsUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ С‚СЂРµРєРёРЅРіР° СЃРµСЃСЃРёРё РјРµРґРёС‚Р°С†РёРё.
     * @return Р­РєР·РµРјРїР»СЏСЂ TrackMeditationSessionUseCase.
     */
    @Provides
    @Singleton
    fun provideTrackMeditationSessionUseCase(repository: MeditationRepository): TrackMeditationSessionUseCase {
        return TrackMeditationSessionUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
     * @return Р­РєР·РµРјРїР»СЏСЂ GetBreathingPatternsUseCase.
     */
    @Provides
    @Singleton
    fun provideGetBreathingPatternsUseCase(repository: BreathingRepository): GetBreathingPatternsUseCase {
        return GetBreathingPatternsUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёС… РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
     * @return Р­РєР·РµРјРїР»СЏСЂ GetCustomBreathingPatternsUseCase.
     */
    @Provides
    @Singleton
    fun provideGetCustomBreathingPatternsUseCase(
        repository: com.example.spybrain.domain.repository.CustomBreathingPatternRepository
    ): GetCustomBreathingPatternsUseCase {
        return GetCustomBreathingPatternsUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ С‚СЂРµРєРёРЅРіР° РґС‹С…Р°С‚РµР»СЊРЅРѕР№ СЃРµСЃСЃРёРё.
     * @return Р­РєР·РµРјРїР»СЏСЂ TrackBreathingSessionUseCase.
     */
    @Provides
    @Singleton
    fun provideTrackBreathingSessionUseCase(repository: BreathingRepository): TrackBreathingSessionUseCase {
        return TrackBreathingSessionUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РѕР±С‰РµР№ СЃС‚Р°С‚РёСЃС‚РёРєРё.
     * @return Р­РєР·РµРјРїР»СЏСЂ GetOverallStatsUseCase.
     */
    @Provides
    @Singleton
    fun provideGetOverallStatsUseCase(repository: StatsRepository): GetOverallStatsUseCase {
        return GetOverallStatsUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РёСЃС‚РѕСЂРёРё СЃРµСЃСЃРёР№.
     * @return Р­РєР·РµРјРїР»СЏСЂ GetSessionHistoryUseCase.
     */
    @Provides
    @Singleton
    fun provideGetSessionHistoryUseCase(repository: StatsRepository): GetSessionHistoryUseCase {
        return GetSessionHistoryUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ СЃРѕС…СЂР°РЅРµРЅРёСЏ СЃРµСЃСЃРёРё.
     * @return Р­РєР·РµРјРїР»СЏСЂ SaveSessionUseCase.
     */
    @Provides
    @Singleton
    fun provideSaveSessionUseCase(repository: StatsRepository): SaveSessionUseCase {
        return SaveSessionUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РёСЃС‚РѕСЂРёРё РґС‹С…Р°С‚РµР»СЊРЅС‹С… СЃРµСЃСЃРёР№.
     * @return Р­РєР·РµРјРїР»СЏСЂ GetBreathingHistoryUseCase.
     */
    @Provides
    @Singleton
    fun provideGetBreathingHistoryUseCase(repository: StatsRepository): GetBreathingHistoryUseCase {
        return GetBreathingHistoryUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ СЃРѕС…СЂР°РЅРµРЅРёСЏ РґС‹С…Р°С‚РµР»СЊРЅРѕР№ СЃРµСЃСЃРёРё.
     * @return Р­РєР·РµРјРїР»СЏСЂ SaveBreathingSessionUseCase.
     */
    @Provides
    @Singleton
    fun provideSaveBreathingSessionUseCase(repository: StatsRepository): SaveBreathingSessionUseCase {
        return SaveBreathingSessionUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ РґРѕР±Р°РІР»РµРЅРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРѕРіРѕ РґС‹С…Р°С‚РµР»СЊРЅРѕРіРѕ РїР°С‚С‚РµСЂРЅР°.
     * @return Р­РєР·РµРјРїР»СЏСЂ AddCustomBreathingPatternUseCase.
     */
    @Provides
    @Singleton
    fun provideAddCustomBreathingPatternUseCase(
        repository: com.example.spybrain.domain.repository.CustomBreathingPatternRepository
    ): com.example.spybrain.domain.usecase.breathing.AddCustomBreathingPatternUseCase {
        return AddCustomBreathingPatternUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ СѓРґР°Р»РµРЅРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРѕРіРѕ РґС‹С…Р°С‚РµР»СЊРЅРѕРіРѕ РїР°С‚С‚РµСЂРЅР°.
     * @return Р­РєР·РµРјРїР»СЏСЂ DeleteCustomBreathingPatternUseCase.
     */
    @Provides
    @Singleton
    fun provideDeleteCustomBreathingPatternUseCase(
        repository: com.example.spybrain.domain.repository.CustomBreathingPatternRepository
    ): com.example.spybrain.domain.usecase.breathing.DeleteCustomBreathingPatternUseCase {
        return DeleteCustomBreathingPatternUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РїСЂРѕРіСЂР°РјРј РјРµРґРёС‚Р°С†РёРё.
     * @return Р­РєР·РµРјРїР»СЏСЂ GetMeditationProgramsUseCase.
     */
    @Provides
    @Singleton
    fun provideGetMeditationProgramsUseCase(
        repository: com.example.spybrain.domain.repository.MeditationProgramRepository
    ): com.example.spybrain.domain.usecase.meditation.GetMeditationProgramsUseCase {
        return com.example.spybrain.domain.usecase.meditation.GetMeditationProgramsUseCase(repository)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ use case РґР»СЏ РїРѕР»СѓС‡РµРЅРёСЏ РґРѕСЃС‚РёР¶РµРЅРёР№.
     * @return Р­РєР·РµРјРїР»СЏСЂ GetAchievementsUseCase.
     */
    @Provides
    @Singleton
    fun provideGetAchievementsUseCase(
        repository: com.example.spybrain.domain.repository.AchievementsRepository
    ): com.example.spybrain.domain.usecase.achievements.GetAchievementsUseCase {
        return com.example.spybrain.domain.usecase.achievements.GetAchievementsUseCase(repository)
    }
}
