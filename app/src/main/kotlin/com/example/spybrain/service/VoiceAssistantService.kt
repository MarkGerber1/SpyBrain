package com.example.spybrain.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.spybrain.data.datastore.SettingsDataStore
import com.example.spybrain.util.VibrationUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import com.example.spybrain.R
import com.example.spybrain.domain.service.IVoiceAssistant

@Singleton
class VoiceAssistantService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsDataStore: SettingsDataStore? = null
) : IVoiceAssistant {
    
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private val scope = CoroutineScope(Dispatchers.Main)
    
    // Доступные голоса
    private val availableVoices = mutableListOf<Voice>()
    private var currentVoice: Voice? = null
    
    init {
        initializeTTS()
    }
    
    private fun initializeTTS() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                setupVoices()
                Timber.d("TTS инициализирован успешно")
            } else {
                Timber.e("Ошибка инициализации TTS: $status")
            }
        }
    }
    
    private fun setupVoices() {
        textToSpeech?.let { tts ->
            // Получаем доступные голоса
            val voices = tts.voices?.filter { voice ->
                // Фильтруем только качественные голоса
                voice.quality >= Voice.QUALITY_NORMAL &&
                (voice.locale == Locale("ru", "RU") || 
                 voice.locale == Locale.US || 
                 voice.locale == Locale.UK)
            } ?: emptyList()
            
            availableVoices.clear()
            availableVoices.addAll(voices)
            
            // Устанавливаем дефолтный голос
            scope.launch {
                val voiceId = settingsDataStore?.getVoiceId() ?: ""
                setVoice(voiceId)
            }
            
            Timber.d("Доступно голосов: ${availableVoices.size}")
        }
    }
    
    fun getAvailableVoices(): List<Voice> = availableVoices
    
    fun setVoice(voiceId: String) {
        textToSpeech?.let { tts ->
            val voice = availableVoices.find { it.name == voiceId }
            if (voice != null) {
                currentVoice = voice
                tts.voice = voice
                Timber.d("Голос установлен: ${voice.name}")
            } else {
                // Устанавливаем дефолтный голос
                val defaultVoice = availableVoices.firstOrNull { 
                    it.locale == Locale("ru", "RU") 
                } ?: availableVoices.firstOrNull()
                
                defaultVoice?.let {
                    currentVoice = it
                    tts.voice = it
                    Timber.d("Установлен дефолтный голос: ${it.name}")
                }
            }
        }
    }
    
    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (!isInitialized) {
            Timber.w("TTS не инициализирован")
            return
        }
        
        textToSpeech?.let { tts ->
            // Настраиваем параметры для более естественного звучания
            tts.setSpeechRate(0.85f) // Немного медленнее для естественности
            tts.setPitch(1.0f) // Нормальная высота
            
            // Добавляем вибрацию если включена
            scope.launch {
                val vibrationEnabled = settingsDataStore?.getVibrationEnabled() ?: false
                if (vibrationEnabled) {
                    VibrationUtil.shortVibration(context)
                }
            }
            
            val result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
            
            if (result == TextToSpeech.SUCCESS) {
                Timber.d("TTS: $text")
                onComplete?.invoke()
            } else {
                Timber.e("Ошибка TTS: $result")
            }
        }
    }
    
    fun speakWithEmotion(text: String, emotion: Emotion = Emotion.NEUTRAL) {
        textToSpeech?.let { tts ->
            when (emotion) {
                Emotion.CALM -> {
                    tts.setSpeechRate(0.75f)
                    tts.setPitch(0.9f)
                }
                Emotion.ENERGETIC -> {
                    tts.setSpeechRate(1.1f)
                    tts.setPitch(1.1f)
                }
                Emotion.GENTLE -> {
                    tts.setSpeechRate(0.7f)
                    tts.setPitch(0.85f)
                }
                Emotion.NEUTRAL -> {
                    tts.setSpeechRate(0.85f)
                    tts.setPitch(1.0f)
                }
            }
            
            speak(text)
        }
    }
    
    fun speakBreathingInstruction(instruction: String) {
        speakWithEmotion(instruction, Emotion.CALM)
    }
    
    fun speakMeditationGuidance(guidance: String) {
        speakWithEmotion(guidance, Emotion.GENTLE)
    }
    
    fun speakAchievement(achievement: String) {
        speakWithEmotion(achievement, Emotion.ENERGETIC)
    }
    
    fun stop() {
        textToSpeech?.stop()
    }
    
    fun shutdown() {
        textToSpeech?.shutdown()
        isInitialized = false
    }
    
    override fun isReady(): Boolean = isInitialized
    
    fun getVoiceId(): String {
        return currentVoice?.name ?: ""
    }
    
    fun getVoiceDescription(voice: android.speech.tts.Voice): String {
        return try {
            val quality = when (voice.quality) {
                android.speech.tts.Voice.QUALITY_HIGH -> "Высокое"
                android.speech.tts.Voice.QUALITY_NORMAL -> "Среднее"
                android.speech.tts.Voice.QUALITY_LOW -> "Низкое"
                else -> "Неизвестное"
            }
            
            val gender = when {
                voice.name.contains("female", ignoreCase = true) -> "Женский"
                voice.name.contains("male", ignoreCase = true) -> "Мужской"
                else -> "Нейтральный"
            }
            
            "${voice.name} ($gender, $quality качество, ${voice.locale.displayLanguage})"
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при получении описания голоса")
            voice.name
        }
    }
    
    fun setVoiceById(voiceId: String) {
        setVoice(voiceId)
    }
    
    enum class Emotion {
        CALM, ENERGETIC, GENTLE, NEUTRAL
    }
    
    override fun release() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            scope.cancel()
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
        
        textToSpeech?.let { tts ->
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
    
    override fun speak(text: String) {
        speakBreathingPrompt(text)
    }
    
    override fun startListening(onResult: (String) -> Unit) {
        // TODO: Реализовать распознавание речи
        // Пока что просто логируем
        Timber.d("Voice listening started (not implemented yet)")
    }
} 