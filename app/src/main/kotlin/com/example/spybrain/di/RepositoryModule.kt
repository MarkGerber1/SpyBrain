package com.example.spybrain.di

import com.example.spybrain.data.repository.BreathingRepositoryImpl
import com.example.spybrain.data.repository.MeditationRepositoryImpl
import com.example.spybrain.data.repository.ProfileRepositoryImpl
import com.example.spybrain.data.repository.SettingsRepositoryImpl
import com.example.spybrain.data.repository.StatsRepositoryImpl
import com.example.spybrain.data.repository.CustomBreathingPatternRepositoryImpl
import com.example.spybrain.data.repository.MeditationProgramRepositoryImpl
import com.example.spybrain.data.repository.AchievementsRepositoryImpl
import com.example.spybrain.data.repository.ReminderRepositoryImpl
import com.example.spybrain.data.repository.BreathingPatternRepository
import com.example.spybrain.data.repository.HeartRateRepository
import com.example.spybrain.domain.repository.BreathingRepository
import com.example.spybrain.domain.repository.MeditationRepository
import com.example.spybrain.domain.repository.ProfileRepository
import com.example.spybrain.domain.repository.SettingsRepository
import com.example.spybrain.domain.repository.StatsRepository
import com.example.spybrain.domain.repository.CustomBreathingPatternRepository
import com.example.spybrain.domain.repository.AchievementsRepository
import com.example.spybrain.domain.repository.ReminderRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * DI-РјРѕРґСѓР»СЊ РґР»СЏ Р±РёРЅРґРёРЅРіР° СЂРµРїРѕР·РёС‚РѕСЂРёРµРІ.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindsModule {

    /**
     * Р‘РёРЅРґРёРЅРі SettingsRepository.
     */
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    /**
     * Р‘РёРЅРґРёРЅРі ProfileRepository.
     */
    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    /**
     * Р‘РёРЅРґРёРЅРі MeditationRepository.
     */
    @Binds
    @Singleton
    abstract fun bindMeditationRepository(impl: MeditationRepositoryImpl): MeditationRepository

    /**
     * Р‘РёРЅРґРёРЅРі BreathingRepository.
     */
    @Binds
    @Singleton
    abstract fun bindBreathingRepository(impl: BreathingRepositoryImpl): BreathingRepository

    /**
     * Р‘РёРЅРґРёРЅРі StatsRepository.
     */
    @Binds
    @Singleton
    abstract fun bindStatsRepository(impl: StatsRepositoryImpl): StatsRepository

    /**
     * Р‘РёРЅРґРёРЅРі CustomBreathingPatternRepository.
     */
    @Binds
    @Singleton
    abstract fun bindCustomBreathingPatternRepository(
        impl: CustomBreathingPatternRepositoryImpl
    ): CustomBreathingPatternRepository

    /**
     * Р‘РёРЅРґРёРЅРі MeditationProgramRepository.
     */
    @Binds
    @Singleton
    abstract fun bindMeditationProgramRepository(
        impl: MeditationProgramRepositoryImpl
    ): com.example.spybrain.domain.repository.MeditationProgramRepository

    /**
     * Р‘РёРЅРґРёРЅРі AchievementsRepository.
     */
    @Binds
    @Singleton
    abstract fun bindAchievementsRepository(
        impl: AchievementsRepositoryImpl
    ): AchievementsRepository

    /**
     * Р‘РёРЅРґРёРЅРі ReminderRepository.
     */
    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        impl: ReminderRepositoryImpl
    ): ReminderRepository
}

/**
 * DI-РјРѕРґСѓР»СЊ РґР»СЏ РїСЂРµРґРѕСЃС‚Р°РІР»РµРЅРёСЏ Р·Р°РІРёСЃРёРјРѕСЃС‚РµР№ СЂРµРїРѕР·РёС‚РѕСЂРёРµРІ.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryProvidesModule {

    /**
     * РџСЂРѕРІР°Р№РґРµСЂ BreathingPatternRepository.
     */
    @Provides
    @Singleton
    fun provideBreathingPatternRepository(@ApplicationContext context: android.content.Context): BreathingPatternRepository {
        return BreathingPatternRepository(context)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ СЂРµР°Р»РёР·Р°С†РёСЋ HeartRateRepository.
     * @return Р­РєР·РµРјРїР»СЏСЂ HeartRateRepository.
     */
    @Provides
    @Singleton
    fun provideHeartRateRepository(
        heartRateDao: com.example.spybrain.data.storage.dao.HeartRateDao,
        settingsDataStore: com.example.spybrain.data.datastore.SettingsDataStore
    ): HeartRateRepository {
        return HeartRateRepository(heartRateDao, settingsDataStore)
    }
}
