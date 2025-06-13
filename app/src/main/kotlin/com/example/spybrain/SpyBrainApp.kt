package com.example.spybrain

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import com.example.spybrain.BuildConfig

@HiltAndroidApp
class SpyBrainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Инициализация Timber для логирования
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
} 