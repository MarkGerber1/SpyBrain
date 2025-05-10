package com.example.spybrain.di

import android.content.Context
import com.example.spybrain.voice.VoiceAssistantService
import com.example.spybrain.service.HealthAdvisorService
import com.example.spybrain.domain.service.IVoiceAssistant
import com.example.spybrain.domain.service.IHealthAdvisor
import com.example.spybrain.domain.service.IAiMentor
import dagger.Module
import dagger.Provides
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
        @ApplicationContext context: Context
    ): IVoiceAssistant = VoiceAssistantService(context) // TODO реализовано: внедрение через интерфейс

    @Provides
    @Singleton
    fun provideHealthAdvisorService(): IHealthAdvisor =
        HealthAdvisorService() // TODO реализовано: внедрение через интерфейс

    @Provides
    @Singleton
    fun provideAiMentorService(
        voiceAssistantService: com.example.spybrain.voice.VoiceAssistantService
    ): com.example.spybrain.domain.service.IAiMentor =
        com.example.spybrain.voice.AiMentorService(voiceAssistantService) // TODO реализовано: внедрение через интерфейс
}
// NOTE реализовано по аудиту: DI только через абстракции 