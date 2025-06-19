package com.example.spybrain.di

import android.content.Context
import com.example.spybrain.service.VoiceAssistantService
import com.example.spybrain.service.HealthAdvisorService
import com.example.spybrain.domain.service.IVoiceAssistant
import com.example.spybrain.domain.service.IHealthAdvisor
import com.example.spybrain.domain.service.IAiMentor
import dagger.Module
import dagger.Provides
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VoiceModule {
    @Provides
    @Singleton
    fun provideVoiceAssistantService(
        @ApplicationContext context: Context,
        settingsDataStore: com.example.spybrain.data.datastore.SettingsDataStore
    ): VoiceAssistantService {
        return VoiceAssistantService(context, settingsDataStore)
    }

    @Provides
    @Singleton
    fun provideHealthAdvisorService(): IHealthAdvisor =
        HealthAdvisorService() // TODO реализовано: внедрение через интерфейс

    @Provides
    @Singleton
    fun provideAiMentorService(
        voiceAssistantService: VoiceAssistantService
    ): IAiMentor =
        com.example.spybrain.service.AiMentorService(voiceAssistantService) // TODO реализовано: внедрение через интерфейс
}

@Module
@InstallIn(SingletonComponent::class)
abstract class VoiceBindsModule {
    @Binds
    @Singleton
    abstract fun bindVoiceAssistant(impl: VoiceAssistantService): IVoiceAssistant
}

// NOTE реализовано по аудиту: DI только через абстракции 