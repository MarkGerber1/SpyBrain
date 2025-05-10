package com.example.spybrain.domain.service

// TODO реализовано: Абстракция для сервиса советов по здоровью
interface IHealthAdvisor {
    fun getAdvices(phase: Any, bpm: Int): List<String>
}
// NOTE реализовано по аудиту: IHealthAdvisor для DI и ViewModel 