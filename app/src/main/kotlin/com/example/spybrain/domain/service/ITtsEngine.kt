package com.example.spybrain.domain.service

import android.speech.tts.Voice

/**
 * Abstraction over Android TextToSpeech to enable unit testing without platform services.
 */
interface ITtsEngine {
    fun initialize(onReady: () -> Unit)
    fun isInitialized(): Boolean

    fun speak(text: String, utteranceId: String = "")
    fun stop()
    fun shutdown()

    fun setSpeechRate(rate: Float)
    fun setPitch(pitch: Float)

    fun getVoices(): List<Voice>
    fun setVoice(voice: Voice)
}



