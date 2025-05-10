package com.example.spybrain.voice

import android.content.Context
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.os.Bundle
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import com.example.spybrain.domain.service.IVoiceAssistant
import android.speech.tts.Voice

@Singleton
class VoiceAssistantService @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener, IVoiceAssistant {
    private val tts = TextToSpeech(context, this)

    override fun onInit(status: Int) { /* no-op */ }

    override fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun startListening(onResult: (String) -> Unit) {
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) { /* no-op */ }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onResults(results: Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val commandText = matches?.firstOrNull()?.lowercase() ?: return
                when {
                    "дых" in commandText || "start breathing" in commandText -> { // TODO: Вынести команды в ресурсы или отдельный конфиг для локализации
                        speak("Запускаю дыхательную практику") // TODO: Вынести строку в strings.xml
                        onResult("StartBreathing")
                    }
                    "медита" in commandText || "start meditation" in commandText -> { // TODO: Вынести команды в ресурсы или отдельный конфиг для локализации
                        speak("Запускаю медитацию") // TODO: Вынести строку в strings.xml
                        onResult("StartMeditation")
                    }
                    "стат" in commandText || "show stats" in commandText -> { // TODO: Вынести команды в ресурсы или отдельный конфиг для локализации
                        speak("Показываю статистику") // TODO: Вынести строку в strings.xml
                        onResult("ShowStats")
                    }
                    else -> speak("Команда не распознана") // TODO: Вынести строку в strings.xml
                }
            }
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer.startListening(intent)
    }

    fun getAvailableVoices(): List<Voice> = tts.voices?.filter { it.locale.language == "ru" }?.toList() ?: emptyList() // FIXME: Хардкод языка "ru". Использовать текущую локаль приложения.
    fun setVoiceById(voiceId: String) {
        val voice = tts.voices?.find { it.locale.language == "ru" && it.name == voiceId } // FIXME: Хардкод языка "ru". Использовать текущую локаль приложения.
        if (voice != null) tts.voice = voice
    }
    fun getVoiceDescription(voice: Voice): String {
        val gender = when {
            voice.name.contains("female", true) || voice.name.contains("fem", true) -> "Женский" // TODO: Вынести строку в strings.xml
            voice.name.contains("male", true) || voice.name.contains("masc", true) -> "Мужской" // TODO: Вынести строку в strings.xml
            else -> "?" // TODO: Вынести строку в strings.xml
        }
        val style = when {
            voice.name.contains("soft", true) || voice.name.contains("smooth", true) -> "мягкий" // TODO: Вынести строку в strings.xml
            voice.name.contains("sexy", true) -> "сексуальный" // TODO: Вынести строку в strings.xml
            voice.name.contains("mystery", true) || voice.name.contains("myst", true) -> "таинственный" // TODO: Вынести строку в strings.xml
            else -> "обычный" // TODO: Вынести строку в strings.xml
        }
        return "$gender, $style (${voice.locale.displayLanguage})" // TODO: Вынести формат строки в strings.xml
    }
} 