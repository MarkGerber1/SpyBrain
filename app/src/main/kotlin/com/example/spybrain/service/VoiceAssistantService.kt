package com.example.spybrain.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.speech.tts.UtteranceProgressListener
import timber.log.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Locale

class VoiceAssistantService(private val context: Context) {
    
    private var tts: TextToSpeech? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private var currentVoice: Voice? = null
    private var isInitialized = false
    
    init {
        initializeTTS()
    }
    
    private fun initializeTTS() {
        try {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isInitialized = true
                    setupDefaultVoice()
                    setupUtteranceListener()
                    Timber.d("TTS initialized successfully")
                } else {
                    Timber.e("TTS initialization failed with status: $status")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize TTS")
        }
    }
    
    private fun setupDefaultVoice() {
        try {
            // Пытаемся найти лучший голос для русского языка
            val voices = tts?.voices ?: return
            
            // Приоритет: русский женский голос
            currentVoice = voices.find { voice ->
                voice.locale.language == "ru" && voice.name.contains("female", ignoreCase = true)
            } ?: voices.find { voice ->
                voice.locale.language == "ru"
            } ?: voices.find { voice ->
                voice.locale.language == "en" && voice.name.contains("female", ignoreCase = true)
            } ?: voices.firstOrNull()
            
            currentVoice?.let { voice ->
                tts?.voice = voice
                tts?.language = voice.locale
                Timber.d("Selected voice: ${voice.name} (${voice.locale})")
            }
            
            // Настройка параметров качества
            tts?.setSpeechRate(0.85f) // Немного медленнее для лучшего понимания
            tts?.setPitch(1.0f) // Нормальная высота
            
        } catch (e: Exception) {
            Timber.e(e, "Failed to setup default voice")
        }
    }
    
    private fun setupUtteranceListener() {
        try {
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Timber.d("TTS started: $utteranceId")
                }
                
                override fun onDone(utteranceId: String?) {
                    Timber.d("TTS completed: $utteranceId")
                }
                
                override fun onError(utteranceId: String?) {
                    Timber.e("TTS error: $utteranceId")
                }
            })
        } catch (e: Exception) {
            Timber.e(e, "Failed to setup utterance listener")
        }
    }
    
    fun getAvailableVoices(): List<Voice> {
        return try {
            tts?.voices?.filter { voice ->
                // Фильтруем только качественные голоса
                voice.quality >= Voice.QUALITY_NORMAL &&
                (voice.locale.language == "ru" || voice.locale.language == "en")
            }?.sortedBy { it.name } ?: emptyList()
        } catch (e: Exception) {
            Timber.e(e, "Failed to get available voices")
            emptyList()
        }
    }
    
    fun getVoiceDescription(voice: Voice): String {
        return try {
            val quality = when (voice.quality) {
                Voice.QUALITY_HIGH -> "Высокое"
                Voice.QUALITY_NORMAL -> "Среднее"
                Voice.QUALITY_LOW -> "Низкое"
                else -> "Неизвестное"
            }
            
            val gender = when {
                voice.name.contains("female", ignoreCase = true) -> "Женский"
                voice.name.contains("male", ignoreCase = true) -> "Мужской"
                else -> "Неопределенный"
            }
            
            "${voice.name} ($gender, $quality качество, ${voice.locale.displayLanguage})"
        } catch (e: Exception) {
            Timber.e(e, "Failed to get voice description")
            voice.name
        }
    }
    
    fun setVoiceById(voiceId: String) {
        try {
            val voices = getAvailableVoices()
            val selectedVoice = voices.find { it.name == voiceId }
            
            selectedVoice?.let { voice ->
                currentVoice = voice
                tts?.voice = voice
                tts?.language = voice.locale
                Timber.d("Voice set to: ${voice.name}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to set voice")
        }
    }
    
    fun speak(text: String, utteranceId: String = "default") {
        try {
            if (!isInitialized) {
                Timber.w("TTS not initialized yet")
                return
            }
            
            serviceScope.launch {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to speak text")
        }
    }
    
    fun speakWithDelay(text: String, delayMs: Long = 1000) {
        try {
            serviceScope.launch {
                kotlinx.coroutines.delay(delayMs)
                speak(text)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to speak with delay")
        }
    }
    
    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop TTS")
        }
    }
    
    fun setSpeechRate(rate: Float) {
        try {
            tts?.setSpeechRate(rate.coerceIn(0.1f, 2.0f))
        } catch (e: Exception) {
            Timber.e(e, "Failed to set speech rate")
        }
    }
    
    fun setPitch(pitch: Float) {
        try {
            tts?.setPitch(pitch.coerceIn(0.1f, 2.0f))
        } catch (e: Exception) {
            Timber.e(e, "Failed to set pitch")
        }
    }
    
    fun isSpeaking(): Boolean {
        return try {
            tts?.isSpeaking == true
        } catch (e: Exception) {
            Timber.e(e, "Failed to check speaking status")
            false
        }
    }
    
    fun getCurrentVoice(): Voice? {
        return currentVoice
    }
    
    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
            serviceScope.cancel()
            Timber.d("VoiceAssistantService released")
        } catch (e: Exception) {
            Timber.e(e, "Failed to release TTS")
        }
    }
} 