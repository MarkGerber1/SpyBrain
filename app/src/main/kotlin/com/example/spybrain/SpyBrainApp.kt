package com.example.spybrain

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import com.example.spybrain.BuildConfig

@HiltAndroidApp
class SpyBrainApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // РРЅРёС†РёР°Р»РёР·Р°С†РёСЏ Timber РґР»СЏ Р»РѕРіРёСЂРѕРІР°РЅРёСЏ
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
