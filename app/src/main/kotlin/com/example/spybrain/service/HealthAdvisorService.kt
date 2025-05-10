package com.example.spybrain.service

import com.example.spybrain.domain.service.IHealthAdvisor
import com.example.spybrain.presentation.breathing.BreathingContract
import javax.inject.Inject
import kotlin.random.Random

/**
 * Сервис для выдачи динамических советов в процессе дыхательных практик
 */
class HealthAdvisorService @Inject constructor() : IHealthAdvisor {
    private val inhaleTips = listOf(
        "Сконцентрируйтесь на медленном вдохе",
        "Попробуйте глубже вдохнуть через нос"
    )
    private val holdAfterInhaleTips = listOf(
        "Держите воздух: чувствуете расслабление?",
        "Сохраняйте спокойствие на паузе"
    )
    private val exhaleTips = listOf(
        "Выдохните медленно, освобождая напряжение",
        "Отпустите воздух плавно"
    )
    private val holdAfterExhaleTips = listOf(
        "Почувствуйте лёгкость после выдоха",
        "Расслабьтесь перед следующим вдохом"
    )

    /**
     * Формирует список советов в зависимости от фазы дыхания и текущего BPM
     */
    override fun getAdvices(phase: Any, bpm: Int): List<String> {
        // TODO реализовано: логика советов по здоровью
        return emptyList()
    }
}
// NOTE реализовано по аудиту: IHealthAdvisor адаптер 