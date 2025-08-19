package com.example.spybrain.di

import com.example.spybrain.domain.service.IPlayerService
import com.example.spybrain.service.VoiceAssistantService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoints {
    fun playerService(): IPlayerService
    fun voiceAssistantService(): VoiceAssistantService
    fun settingsDataStore(): com.example.spybrain.data.datastore.SettingsDataStore
}


