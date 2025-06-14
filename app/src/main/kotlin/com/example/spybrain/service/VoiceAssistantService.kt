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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceAssistantService @Inject constructor(
    private val context: Context
) {
    
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
                    setupVoice()
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
    
    private fun setupVoice() {
        try {
            // Устанавливаем русский язык
            val result = tts?.setLanguage(Locale("ru", "RU"))
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Если русский не поддерживается, используем английский
                tts?.setLanguage(Locale.US)
                Timber.w("Russian language not supported, using English")
            }
            
            // Ищем мягкий женский голос
            val voices = tts?.voices
            val softFemaleVoice = voices?.find { voice ->
                voice.name.contains("female", ignoreCase = true) ||
                voice.name.contains("женский", ignoreCase = true) ||
                voice.name.contains("soft", ignoreCase = true) ||
                voice.name.contains("gentle", ignoreCase = true)
            }
            
            softFemaleVoice?.let { voice ->
                tts?.voice = voice
                currentVoice = voice
                Timber.d("Using voice: ${voice.name}")
            }
            
            // Настраиваем параметры для мягкого голоса
            tts?.setSpeechRate(0.8f) // Немного медленнее
            tts?.setPitch(1.1f) // Немного выше для женского голоса
            
        } catch (e: Exception) {
            Timber.e(e, "Failed to setup voice")
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
    
    fun speakBreathingPrompt(prompt: String) {
        if (!isInitialized) {
            Timber.w("TTS not initialized yet")
            return
        }
        
        tts?.let { tts ->
            tts.speak(prompt, TextToSpeech.QUEUE_FLUSH, null, "breathing_prompt")
            Timber.d("Speaking: $prompt")
        }
    }
    
    fun speakInhale() {
        speakBreathingPrompt("Вдох")
    }
    
    fun speakExhale() {
        speakBreathingPrompt("Выдох")
    }
    
    fun speakHold() {
        speakBreathingPrompt("Задержите")
    }
    
    fun speakRelax() {
        speakBreathingPrompt("Расслабьтесь")
    }
    
    fun speakStart() {
        speakBreathingPrompt("Начинаем дыхательную практику")
    }
    
    fun speakComplete() {
        speakBreathingPrompt("Практика завершена. Отлично!")
    }
    
    fun speakCycle(cycle: Int, total: Int) {
        speakBreathingPrompt("Цикл $cycle из $total")
    }
    
    fun speakMotivation() {
        val motivations = listOf(
            "Вы делаете это прекрасно",
            "Продолжайте в том же духе",
            "Ваше дыхание становится все более спокойным",
            "Вы на правильном пути",
            "Каждый вдох приносит вам спокойствие",
            "Вы становитесь сильнее с каждым циклом"
        )
        val randomMotivation = motivations.random()
        speakBreathingPrompt(randomMotivation)
    }
    
    fun shutdown() {
        tts?.shutdown()
        isInitialized = false
    }
    
    fun isReady(): Boolean = isInitialized
} 