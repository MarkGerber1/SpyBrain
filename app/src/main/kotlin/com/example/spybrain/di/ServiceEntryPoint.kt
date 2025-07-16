package com.example.spybrain.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.example.spybrain.service.VoiceAssistantService
import com.example.spybrain.service.HealthAdvisorService

/**
 * EntryPoint РґР»СЏ DI СЃРµСЂРІРёСЃРѕРІ.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface ServiceEntryPoint {
    /**
     * РџРѕР»СѓС‡РёС‚СЊ VoiceAssistantService.
     */
    fun voiceAssistantService(): VoiceAssistantService
    /**
     * РџРѕР»СѓС‡РёС‚СЊ HealthAdvisorService.
     */
    fun healthAdvisorService(): HealthAdvisorService
}
