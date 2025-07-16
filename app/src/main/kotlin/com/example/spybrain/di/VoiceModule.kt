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

/**
 * РњРѕРґСѓР»СЊ Dagger РґР»СЏ РїСЂРµРґРѕСЃС‚Р°РІР»РµРЅРёСЏ РіРѕР»РѕСЃРѕРІС‹С… СЃРµСЂРІРёСЃРѕРІ.
 */
@Module
@InstallIn(SingletonComponent::class)
object VoiceModule {
    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ СЃРµСЂРІРёСЃ РіРѕР»РѕСЃРѕРІРѕРіРѕ Р°СЃСЃРёСЃС‚РµРЅС‚Р°.
     * @return Р­РєР·РµРјРїР»СЏСЂ VoiceAssistantService.
     */
    @Provides
    @Singleton
    fun provideVoiceAssistantService(
        @ApplicationContext context: Context,
        settingsDataStore: com.example.spybrain.data.datastore.SettingsDataStore
    ): VoiceAssistantService {
        return VoiceAssistantService(context, settingsDataStore)
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ СЃРµСЂРІРёСЃ СЃРѕРІРµС‚РЅРёРєР° РїРѕ Р·РґРѕСЂРѕРІСЊСЋ.
     * @return Р­РєР·РµРјРїР»СЏСЂ HealthAdvisorService.
     */
    @Provides
    @Singleton
    fun provideHealthAdvisorService(): IHealthAdvisor =
        HealthAdvisorService() // TODO СЂРµР°Р»РёР·РѕРІР°РЅРѕ: РІРЅРµРґСЂРµРЅРёРµ С‡РµСЂРµР· РёРЅС‚РµСЂС„РµР№СЃ

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ СЃРµСЂРІРёСЃ AI-РјРµРЅС‚РѕСЂР°.
     * @return Р­РєР·РµРјРїР»СЏСЂ AiMentorService.
     */
    @Provides
    @Singleton
    fun provideAiMentorService(
        voiceAssistantService: VoiceAssistantService
    ): IAiMentor =
        com.example.spybrain.service.AiMentorService(voiceAssistantService) // TODO СЂРµР°Р»РёР·РѕРІР°РЅРѕ: РІРЅРµРґСЂРµРЅРёРµ С‡РµСЂРµР· РёРЅС‚РµСЂС„РµР№СЃ
}

/**
 * РњРѕРґСѓР»СЊ Р±РёРЅРґРёРЅРіРѕРІ РґР»СЏ РіРѕР»РѕСЃРѕРІС‹С… РёРЅС‚РµСЂС„РµР№СЃРѕРІ.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class VoiceBindsModule {
    /**
     * Р‘РёРЅРґРёС‚ VoiceAssistant Рє СЂРµР°Р»РёР·Р°С†РёРё.
     * @param impl Р РµР°Р»РёР·Р°С†РёСЏ VoiceAssistantService.
     * @return VoiceAssistant.
     */
    @Binds
    @Singleton
    abstract fun bindVoiceAssistant(impl: VoiceAssistantService): IVoiceAssistant
}

// NOTE СЂРµР°Р»РёР·РѕРІР°РЅРѕ РїРѕ Р°СѓРґРёС‚Сѓ: DI С‚РѕР»СЊРєРѕ С‡РµСЂРµР· Р°Р±СЃС‚СЂР°РєС†РёРё
