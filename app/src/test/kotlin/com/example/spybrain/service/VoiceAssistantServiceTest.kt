package com.example.spybrain.service

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockkConstructor
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertNotNull

class VoiceAssistantServiceTest {
    @get:Rule
    val mainDispatcherRule = com.example.spybrain.test.utils.MainDispatcherRule()
    @Test
    fun `service can be created`() {
        val context = io.mockk.mockk<Context>(relaxed = true)
        val tts = object : com.example.spybrain.domain.service.ITtsEngine {
            override fun initialize(onReady: () -> Unit) = onReady()
            override fun isInitialized() = true
            override fun speak(text: String, utteranceId: String) {}
            override fun stop() {}
            override fun shutdown() {}
            override fun setSpeechRate(rate: Float) {}
            override fun setPitch(pitch: Float) {}
            override fun getVoices(): List<android.speech.tts.Voice> = emptyList()
            override fun setVoice(voice: android.speech.tts.Voice) {}
        }
        val focus = object : com.example.spybrain.domain.service.IAudioFocusManager {
            override fun requestTransientMayDuck(onLoss: () -> Unit, onGain: () -> Unit) {}
            override fun abandon() {}
        }
        val service = VoiceAssistantService(context, null, tts, focus)
        assertNotNull(service)
    }
}

