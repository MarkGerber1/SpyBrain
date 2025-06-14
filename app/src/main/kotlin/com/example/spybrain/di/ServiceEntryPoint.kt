package com.example.spybrain.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.example.spybrain.service.VoiceAssistantService
import com.example.spybrain.service.HealthAdvisorService

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ServiceEntryPoint {
    fun voiceAssistantService(): VoiceAssistantService
    fun healthAdvisorService(): HealthAdvisorService
} 