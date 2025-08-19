package com.example.spybrain.service

import android.content.Context
import android.speech.tts.Voice
import com.example.spybrain.data.datastore.SettingsDataStore
import com.example.spybrain.util.VibrationUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import com.example.spybrain.R
import com.example.spybrain.domain.service.IVoiceAssistant
import com.example.spybrain.domain.service.ITtsEngine
import com.example.spybrain.domain.service.IAudioFocusManager

@Singleton
class VoiceAssistantService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsDataStore: SettingsDataStore? = null,
    private val ttsEngine: ITtsEngine,
    private val audioFocusManager: IAudioFocusManager
) : IVoiceAssistant {

    private var isInitialized = false
    private val scope = CoroutineScope(Dispatchers.Main)
    private var guidanceJob: kotlinx.coroutines.Job? = null

    // Р”РѕСЃС‚СѓРїРЅС‹Рµ РіРѕР»РѕСЃР°
    private val availableVoices = mutableListOf<Voice>()
    private var currentVoice: Voice? = null

    init {
        initializeTTS()
    }

    private fun initializeTTS() {
        ttsEngine.initialize {
                isInitialized = true
                setupVoices()
            Timber.d("TTS initialized")
        }
    }

    private fun setupVoices() {
        // Получаем доступные голоса из движка
        val voices = ttsEngine.getVoices().filter { voice ->
                // Р¤РёР»СЊС‚СЂСѓРµРј С‚РѕР»СЊРєРѕ РєР°С‡РµСЃС‚РІРµРЅРЅС‹Рµ РіРѕР»РѕСЃР°
                voice.quality >= Voice.QUALITY_NORMAL &&
                (voice.locale == Locale("ru", "RU") ||
                 voice.locale == Locale.US ||
                 voice.locale == Locale.UK)
        }

            availableVoices.clear()
            availableVoices.addAll(voices)

        // Устанавливаем дефолтный голос
            scope.launch {
                val voiceId = settingsDataStore?.getVoiceId() ?: ""
                setVoice(voiceId)
            }

        Timber.d("Available voices: ${availableVoices.size}")
    }

    fun getAvailableVoices(): List<Voice> = availableVoices

    fun setVoice(voiceId: String) {
            val voice = availableVoices.find { it.name == voiceId }
            if (voice != null) {
                currentVoice = voice
            ttsEngine.setVoice(voice)
            Timber.d("Voice set: ${voice.name}")
            } else {
            val defaultVoice = availableVoices.firstOrNull { it.locale == Locale("ru", "RU") }
                ?: availableVoices.firstOrNull()
                defaultVoice?.let {
                    currentVoice = it
                ttsEngine.setVoice(it)
                Timber.d("Default voice set: ${it.name}")
            }
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (!isInitialized) {
            Timber.w("TTS РЅРµ РёРЅРёС†РёР°Р»РёР·РёСЂРѕРІР°РЅ")
            return
        }

        audioFocusManager.requestTransientMayDuck(
            onLoss = { pauseGuidance() },
            onGain = { ttsEngine.setSpeechRate(0.85f) }
        )
        // Настраиваем параметры для более естественного звучания
        ttsEngine.setSpeechRate(0.85f)
        ttsEngine.setPitch(1.0f)

            // Р”РѕР±Р°РІР»СЏРµРј РІРёР±СЂР°С†РёСЋ РµСЃР»Рё РІРєР»СЋС‡РµРЅР°
            scope.launch {
                val vibrationEnabled = settingsDataStore?.getVibrationEnabled() ?: false
                if (vibrationEnabled) {
                    VibrationUtil.shortVibration(context)
                }
            }

        ttsEngine.speak(text, utteranceId = "utteranceId")
                Timber.d("TTS: $text")
                onComplete?.invoke()
    }

    fun speakWithEmotion(text: String, emotion: Emotion = Emotion.NEUTRAL) {
            when (emotion) {
                Emotion.CALM -> {
                ttsEngine.setSpeechRate(0.75f)
                ttsEngine.setPitch(0.9f)
                }
                Emotion.ENERGETIC -> {
                ttsEngine.setSpeechRate(1.1f)
                ttsEngine.setPitch(1.1f)
                }
                Emotion.GENTLE -> {
                ttsEngine.setSpeechRate(0.7f)
                ttsEngine.setPitch(0.85f)
                }
                Emotion.NEUTRAL -> {
                ttsEngine.setSpeechRate(0.85f)
                ttsEngine.setPitch(1.0f)
            }
        }
        speak(text)
    }

    fun speakBreathingInstruction(instruction: String) {
        speakWithEmotion(instruction, Emotion.CALM)
    }

    fun speakMeditationGuidance(guidance: String) {
        // Более мягкие параметры для guided-режима
        ttsEngine.setSpeechRate(0.75f)
        ttsEngine.setPitch(0.9f)
        speak(guidance)
    }

    fun speakAchievement(achievement: String) {
        speakWithEmotion(achievement, Emotion.ENERGETIC)
    }

    fun stop() {
        ttsEngine.stop()
        audioFocusManager.abandon()
    }

    fun shutdown() {
        ttsEngine.shutdown()
        isInitialized = false
    }

    override fun isReady(): Boolean = isInitialized

    fun getVoiceId(): String {
        return currentVoice?.name ?: ""
    }

    fun getVoiceDescription(voice: android.speech.tts.Voice): String {
        return try {
            val quality = when (voice.quality) {
                android.speech.tts.Voice.QUALITY_HIGH -> context.getString(R.string.voice_quality_high)
                android.speech.tts.Voice.QUALITY_NORMAL -> context.getString(R.string.voice_quality_normal)
                android.speech.tts.Voice.QUALITY_LOW -> context.getString(R.string.voice_quality_low)
                else -> context.getString(R.string.voice_quality_unknown)
            }

            val gender = when {
                voice.name.contains("female", ignoreCase = true) -> context.getString(R.string.voice_gender_female)
                voice.name.contains("male", ignoreCase = true) -> context.getString(R.string.voice_gender_male)
                else -> context.getString(R.string.voice_gender_unknown)
            }

            "${voice.name} ($gender, $quality, ${voice.locale.displayLanguage})"
        } catch (e: Exception) {
            Timber.e(e, "Error building voice description")
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
            guidanceJob?.cancel()
            ttsEngine.stop()
            ttsEngine.shutdown()
            scope.cancel()
            audioFocusManager.abandon()
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

        ttsEngine.speak(prompt, utteranceId = "breathing_prompt")
            Timber.d("Speaking: $prompt")
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
        speakBreathingPrompt("Сессия завершена")
    }

    fun speakCycle(cycle: Int, total: Int) {
        speakBreathingPrompt(context.getString(R.string.breathing_cycle_message, cycle, total))
    }

    override fun speakMotivation(message: String) {
        speakBreathingPrompt(message)
    }

    override fun speak(text: String) {
        speakBreathingPrompt(text)
    }

    override fun startListening() {
        // Неподдерживаемо без интеграции ASR; безопасный no-op
    }

    // Intro for Meditation tab (Mode A)
    override fun speakIntro() {
        val intro = context.getString(R.string.meditation_intro_text)
        speakWithEmotion(intro, Emotion.GENTLE)
    }

    // Guided prompts for Guided Meditation tab (Mode B)
    override fun startGuidance(loopIntervalSec: Int) {
        guidanceJob?.cancel()
        val prompts = listOf(
            R.string.guidance_prompt_soft_inhale_exhale,
            R.string.guidance_prompt_release_shoulders,
            R.string.guidance_prompt_focus_chest,
            R.string.guidance_prompt_belly_breath,
            R.string.guidance_prompt_return_attention
        ).map { context.getString(it) }

        guidanceJob = scope.launch {
            var index = 0
            while (true) {
                audioFocusManager.requestTransientMayDuck(onLoss = { pauseGuidance() })
                speakMeditationGuidance(prompts[index % prompts.size])
                index++
                kotlinx.coroutines.delay(loopIntervalSec * 1000L)
            }
        }
    }

    override fun pauseGuidance() {
        guidanceJob?.cancel()
    }

    override fun resumeGuidance() {
        if (guidanceJob == null || guidanceJob?.isCancelled == true) {
            startGuidance(loopIntervalSec = 40)
        }
    }

    override fun stopGuidance() {
        guidanceJob?.cancel()
        guidanceJob = null
        audioFocusManager.abandon()
    }
}
