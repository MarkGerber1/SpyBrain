package com.example.spybrain.domain.service

// TODO реализовано: Абстракция для голосового ассистента
interface IVoiceAssistant {
    fun speak(text: String)
    fun startListening(onResult: (String) -> Unit)
    
    // Методы для дыхательных упражнений
    fun isReady(): Boolean
    fun speakStart()
    fun speakInhale()
    fun speakExhale()
    fun speakHold()
    fun speakRelax()
    fun speakBreathingPrompt(prompt: String)
    fun speakMotivation()
    fun speakComplete()
    fun release()
}
// NOTE реализовано по аудиту: IVoiceAssistant для DI и ViewModel 