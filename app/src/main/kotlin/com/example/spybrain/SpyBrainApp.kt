package com.example.spybrain

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
// import timber.log.Timber
import com.example.spybrain.BuildConfig

@HiltAndroidApp
class SpyBrainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Timber отключен: plant() закомментирован для сборки
    }
} 