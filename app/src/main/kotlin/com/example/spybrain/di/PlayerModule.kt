package com.example.spybrain.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import com.example.spybrain.domain.service.IPlayerService
import com.example.spybrain.service.MeditationPlayerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DI-РјРѕРґСѓР»СЊ РґР»СЏ РїСЂРµРґРѕСЃС‚Р°РІР»РµРЅРёСЏ Р·Р°РІРёСЃРёРјРѕСЃС‚РµР№ РїР»РµРµСЂР°.
 */
@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ ExoPlayer.
     */
    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            setHandleAudioBecomingNoisy(true)
        }
    }

    /**
     * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ PlayerService.
     */
    @Provides
    @Singleton
    fun providePlayerService(
        @ApplicationContext context: Context
    ): IPlayerService = MeditationPlayerService()
}
