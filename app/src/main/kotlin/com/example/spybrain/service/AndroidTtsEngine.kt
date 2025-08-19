package com.example.spybrain.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import com.example.spybrain.domain.service.ITtsEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import java.util.Locale

class AndroidTtsEngine @Inject constructor(
    @ApplicationContext private val context: Context
) : ITtsEngine {
    private var tts: TextToSpeech? = null
    private var ready: Boolean = false
    private val pendingQueue: MutableList<Pair<String, String>> = mutableListOf()

    override fun initialize(onReady: () -> Unit) {
        if (tts != null) return
        tts = TextToSpeech(context) { status ->
            ready = status == TextToSpeech.SUCCESS
            if (ready) {
                try {
                    val res = tts?.setLanguage(Locale("ru", "RU"))
                    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.setLanguage(Locale.US)
                    }
                } catch (_: Exception) { }
                // Выполняем onReady и проговариваем отложенные элементы
                try { onReady() } catch (_: Exception) {}
                if (pendingQueue.isNotEmpty()) {
                    val items = pendingQueue.toList()
                    pendingQueue.clear()
                    items.forEach { (text, utteranceId) ->
                        try { tts?.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId) } catch (_: Exception) {}
                    }
                }
            }
        }
    }

    override fun isInitialized(): Boolean = ready

    override fun speak(text: String, utteranceId: String) {
        val engine = tts
        if (!ready || engine == null) {
            // Кладём в очередь и переинициализируем
            pendingQueue.add(text to utteranceId)
            tts = null
            ready = false
            initialize { /* queued items will be spoken on ready */ }
            return
        }
        try {
            val result = engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            if (result == TextToSpeech.ERROR) {
                // Переинициализация при ошибке и повторная попытка
                engine.shutdown()
                tts = null
                ready = false
                pendingQueue.add(text to utteranceId)
                initialize { /* queued items will be spoken on ready */ }
            }
        } catch (_: Exception) {
            try { engine.shutdown() } catch (_: Exception) {}
            tts = null
            ready = false
            pendingQueue.add(text to utteranceId)
            initialize { }
        }
    }

    override fun stop() { tts?.stop() }

    override fun shutdown() { tts?.shutdown(); ready = false }

    override fun setSpeechRate(rate: Float) { tts?.setSpeechRate(rate) }

    override fun setPitch(pitch: Float) { tts?.setPitch(pitch) }

    override fun getVoices(): List<Voice> = tts?.voices?.toList() ?: emptyList()

    override fun setVoice(voice: Voice) { tts?.voice = voice }
}



