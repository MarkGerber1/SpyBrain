package com.example.spybrain.domain.service

// TODO реализовано: Абстракция для голосового ассистента
interface IVoiceAssistant {
    fun speak(text: String)
    fun startListening(onResult: (String) -> Unit)
}
// NOTE реализовано по аудиту: IVoiceAssistant для DI и ViewModel 