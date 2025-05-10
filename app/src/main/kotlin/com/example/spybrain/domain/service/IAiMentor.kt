package com.example.spybrain.domain.service

// TODO реализовано: Абстракция для AI-наставника
interface IAiMentor {
    fun giveMeditationAdvice()
    fun giveBreathingAdvice(phase: Any)
}
// NOTE реализовано по аудиту: IAiMentor для DI и ViewModel 