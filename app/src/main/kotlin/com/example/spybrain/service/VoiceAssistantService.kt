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
import com.example.spybrain.R
import com.example.spybrain.domain.service.IVoiceAssistant
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class VoiceAssistantService @Inject constructor(
    @ApplicationContext private val context: Context
) : IVoiceAssistant {
    
    private var tts: TextToSpeech? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private var currentVoice: Voice? = null
    private var isInitialized = false
    
    init {
        initializeTTS()
    }
    
    private fun initializeTTS() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                
                // Устанавливаем русский язык по умолчанию
                val result = tts?.setLanguage(Locale("ru"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Timber.w("Russian language not supported, falling back to default")
                    tts?.setLanguage(Locale.getDefault())
                }
                
                // Настраиваем параметры речи
                tts?.setSpeechRate(0.8f) // Немного медленнее для лучшего понимания
                tts?.setPitch(1.1f) // Немного выше для более мягкого звучания
                
                // Ищем женский голос
                val voices = tts?.voices
                val femaleVoice = voices?.find { voice ->
                    voice.quality >= Voice.QUALITY_NORMAL &&
                    (voice.name.contains("женский", ignoreCase = true) ||
                     voice.name.contains("female", ignoreCase = true)) &&
                    (voice.locale.language == "ru" || voice.locale.language == "en")
                }
                
                femaleVoice?.let { voice ->
                    currentVoice = voice
                    tts?.voice = voice
                    tts?.language = voice.locale
                    Timber.d("Female voice set: ${voice.name}")
                }
                
                setupUtteranceListener()
                Timber.d("TTS initialized successfully")
            } else {
                Timber.e("TTS initialization failed with status: $status")
            }
        }
    }
    
    private fun setupUtteranceListener() {
        try {
            tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
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
                Voice.QUALITY_HIGH -> context.getString(R.string.voice_quality_high)
                Voice.QUALITY_NORMAL -> context.getString(R.string.voice_quality_normal)
                Voice.QUALITY_LOW -> context.getString(R.string.voice_quality_low)
                else -> context.getString(R.string.voice_quality_unknown)
            }
            
            val gender = when {
                voice.name.contains("female", ignoreCase = true) -> context.getString(R.string.voice_gender_female)
                voice.name.contains("male", ignoreCase = true) -> context.getString(R.string.voice_gender_male)
                else -> context.getString(R.string.voice_gender_unknown)
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
    
    override fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
            serviceScope.cancel()
            Timber.d("VoiceAssistantService released")
        } catch (e: Exception) {
            Timber.e(e, "Failed to release TTS")
        }
    }
    
    override fun speakBreathingPrompt(prompt: String) {
        if (!isInitialized) {
            Timber.w("TTS not initialized yet")
            return
        }
        
        tts?.let { tts ->
            tts.speak(prompt, TextToSpeech.QUEUE_FLUSH, null, "breathing_prompt")
            Timber.d("Speaking: $prompt")
        }
    }
    
    override fun speakInhale() {
        speakBreathingPrompt(context.getString(R.string.breathing_phase_inhale))
    }
    
    override fun speakExhale() {
        speakBreathingPrompt(context.getString(R.string.breathing_phase_exhale))
    }
    
    override fun speakHold() {
        speakBreathingPrompt(context.getString(R.string.breathing_phase_hold))
    }
    
    override fun speakRelax() {
        speakBreathingPrompt(context.getString(R.string.breathing_relax_message))
    }
    
    override fun speakStart() {
        speakBreathingPrompt(context.getString(R.string.breathing_start_message))
    }
    
    override fun speakComplete() {
        speakBreathingPrompt(context.getString(R.string.breathing_complete_message))
    }
    
    fun speakCycle(cycle: Int, total: Int) {
        speakBreathingPrompt(context.getString(R.string.breathing_cycle_message, cycle, total))
    }
    
    override fun speakMotivation() {
        val motivations = listOf(
            context.getString(R.string.motivation_doing_great),
            context.getString(R.string.motivation_keep_going),
            context.getString(R.string.motivation_breathing_calmer),
            context.getString(R.string.motivation_right_path),
            context.getString(R.string.motivation_breath_brings_calm),
            context.getString(R.string.motivation_stronger_each_cycle)
        )
        val randomMotivation = motivations.random()
        speakBreathingPrompt(randomMotivation)
    }
    
    fun shutdown() {
        tts?.shutdown()
        isInitialized = false
    }
    
    override fun isReady(): Boolean = isInitialized
    
    // Реализация интерфейса IVoiceAssistant
    override fun speak(text: String) {
        speakBreathingPrompt(text)
    }
    
    override fun startListening(onResult: (String) -> Unit) {
        // TODO: Реализовать распознавание речи
        // Пока что просто логируем
        Timber.d("Voice listening started (not implemented yet)")
    }
} 