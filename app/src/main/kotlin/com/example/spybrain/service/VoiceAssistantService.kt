package com.example.spybrain.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
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
    private var guidanceJob: kotlinx.coroutines.Job? = null
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var focusRequest: AudioFocusRequest? = null
    private var hasAudioFocus: Boolean = false

    // Р”РѕСЃС‚СѓРїРЅС‹Рµ РіРѕР»РѕСЃР°
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
                Timber.d("TTS РёРЅРёС†РёР°Р»РёР·РёСЂРѕРІР°РЅ СѓСЃРїРµС€РЅРѕ")
            } else {
                Timber.e("РћС€РёР±РєР° РёРЅРёС†РёР°Р»РёР·Р°С†РёРё TTS: $status")
            }
        }
    }

    private fun setupVoices() {
        textToSpeech?.let { tts ->
            // РџРѕР»СѓС‡Р°РµРј РґРѕСЃС‚СѓРїРЅС‹Рµ РіРѕР»РѕСЃР°
            val voices = tts.voices?.filter { voice ->
                // Р¤РёР»СЊС‚СЂСѓРµРј С‚РѕР»СЊРєРѕ РєР°С‡РµСЃС‚РІРµРЅРЅС‹Рµ РіРѕР»РѕСЃР°
                voice.quality >= Voice.QUALITY_NORMAL &&
                (voice.locale == Locale("ru", "RU") ||
                 voice.locale == Locale.US ||
                 voice.locale == Locale.UK)
            } ?: emptyList()

            availableVoices.clear()
            availableVoices.addAll(voices)

            // РЈСЃС‚Р°РЅР°РІР»РёРІР°РµРј РґРµС„РѕР»С‚РЅС‹Р№ РіРѕР»РѕСЃ
            scope.launch {
                val voiceId = settingsDataStore?.getVoiceId() ?: ""
                setVoice(voiceId)
            }

            Timber.d("Р”РѕСЃС‚СѓРїРЅРѕ РіРѕР»РѕСЃРѕРІ: ${availableVoices.size}")
        }
    }

    fun getAvailableVoices(): List<Voice> = availableVoices

    fun setVoice(voiceId: String) {
        textToSpeech?.let { tts ->
            val voice = availableVoices.find { it.name == voiceId }
            if (voice != null) {
                currentVoice = voice
                tts.voice = voice
                Timber.d("Р“РѕР»РѕСЃ СѓСЃС‚Р°РЅРѕРІР»РµРЅ: ${voice.name}")
            } else {
                // РЈСЃС‚Р°РЅР°РІР»РёРІР°РµРј РґРµС„РѕР»С‚РЅС‹Р№ РіРѕР»РѕСЃ
                val defaultVoice = availableVoices.firstOrNull {
                    it.locale == Locale("ru", "RU")
                } ?: availableVoices.firstOrNull()

                defaultVoice?.let {
                    currentVoice = it
                    tts.voice = it
                    Timber.d("РЈСЃС‚Р°РЅРѕРІР»РµРЅ РґРµС„РѕР»С‚РЅС‹Р№ РіРѕР»РѕСЃ: ${it.name}")
                }
            }
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (!isInitialized) {
            Timber.w("TTS РЅРµ РёРЅРёС†РёР°Р»РёР·РёСЂРѕРІР°РЅ")
            return
        }

        textToSpeech?.let { tts ->
            requestAudioFocus()
            // РќР°СЃС‚СЂР°РёРІР°РµРј РїР°СЂР°РјРµС‚СЂС‹ РґР»СЏ Р±РѕР»РµРµ РµСЃС‚РµСЃС‚РІРµРЅРЅРѕРіРѕ Р·РІСѓС‡Р°РЅРёСЏ
            tts.setSpeechRate(0.85f) // РќРµРјРЅРѕРіРѕ РјРµРґР»РµРЅРЅРµРµ РґР»СЏ РµСЃС‚РµСЃС‚РІРµРЅРЅРѕСЃС‚Рё
            tts.setPitch(1.0f) // РќРѕСЂРјР°Р»СЊРЅР°СЏ РІС‹СЃРѕС‚Р°

            // Р”РѕР±Р°РІР»СЏРµРј РІРёР±СЂР°С†РёСЋ РµСЃР»Рё РІРєР»СЋС‡РµРЅР°
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
                Timber.e("РћС€РёР±РєР° TTS: $result")
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
        abandonAudioFocus()
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
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            scope.cancel()
            abandonAudioFocus()
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
                requestAudioFocus()
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
        abandonAudioFocus()
    }

    private fun requestAudioFocus() {
        if (hasAudioFocus) return
        val audioAttrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        val fr = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setOnAudioFocusChangeListener { change ->
                when (change) {
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
                    AudioManager.AUDIOFOCUS_LOSS -> {
                        pauseGuidance()
                    }
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                        textToSpeech?.setSpeechRate(0.8f)
                    }
                    AudioManager.AUDIOFOCUS_GAIN -> {
                        textToSpeech?.setSpeechRate(0.85f)
                    }
                }
            }
            .setAudioAttributes(audioAttrs)
            .build()
        val res = audioManager.requestAudioFocus(fr)
        if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            focusRequest = fr
            hasAudioFocus = true
        }
    }

    private fun abandonAudioFocus() {
        focusRequest?.let {
            audioManager.abandonAudioFocusRequest(it)
        }
        focusRequest = null
        hasAudioFocus = false
    }
}
