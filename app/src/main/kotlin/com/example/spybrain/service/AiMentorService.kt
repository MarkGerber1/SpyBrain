package com.example.spybrain.service

import javax.inject.Inject
import javax.inject.Singleton
import com.example.spybrain.presentation.breathing.BreathingContract
import com.example.spybrain.domain.service.IAiMentor
import kotlin.random.Random

@Singleton
class AiMentorService @Inject constructor(
    private val voiceAssistantService: VoiceAssistantService
) : IAiMentor {
    private val inhaleAdvices = listOf(
        "Ты молодец, попробуй вдохнуть чуть глубже.",
        "Сфокусируйся на положении диафрагмы при вдохе."
    )
    private val holdAfterInhaleAdvices = listOf(
        "Отлично держишь паузу, сохраняй спокойствие.",
        "Попробуй почувствовать, как воздух заполняет тебя."
    )
    private val exhaleAdvices = listOf(
        "Медленно выдохни, освобождая все напряжение.",
        "Сосредоточься на спокойном выдохе."
    )
    private val holdAfterExhaleAdvices = listOf(
        "Расслабься и позволь телу отдохнуть.",
        "Почувствуй волны спокойствия в теле."
    )
    private val meditationAdvices = listOf(
        "Почувствуй звук музыки внутри себя.",
        "Отпускай мысли и возвращайся к дыханию.",
        "Сосредоточься на текущем моменте.",
        "Позволь себе просто быть здесь и сейчас.",
        "Наблюдай за своими ощущениями без оценок.",
        "Дыши естественно и спокойно.",
        "Каждый вдох приносит покой, каждый выдох - расслабление."
    )
    
    private val breathingAdvices = listOf(
        "Начни с медленного глубокого вдоха через нос.",
        "Попробуй технику 4-7-8 для расслабления.",
        "Дыши животом, а не грудью - это более естественно.",
        "Синхронизируй дыхание с сердцебиением.",
        "Представь, как с каждым вдохом ты наполняешься энергией.",
        "Короткие сессии регулярно лучше, чем долгие редко.",
        "Найди свой комфортный ритм дыхания."
    )

    override fun giveBreathingAdvice(userId: String): String {
        // В будущем здесь будет персонализация на основе истории пользователя
        return breathingAdvices.random()
    }

    override fun giveMeditationAdvice(userId: String): String {
        // В будущем здесь будет персонализация на основе прогресса
        return meditationAdvices.random()
    }
    
    /**
     * Получить совет для конкретной фазы дыхания
     */
    fun getBreathingPhaseAdvice(phase: BreathingContract.BreathPhase): String {
        return when (phase) {
            BreathingContract.BreathPhase.INHALE -> inhaleAdvices.random()
            BreathingContract.BreathPhase.HOLD_AFTER_INHALE -> holdAfterInhaleAdvices.random()
            BreathingContract.BreathPhase.EXHALE -> exhaleAdvices.random()
            BreathingContract.BreathPhase.HOLD_AFTER_EXHALE -> holdAfterExhaleAdvices.random()
        }
    }
    
    /**
     * Получить мотивационное сообщение на основе прогресса
     */
    fun getMotivationalMessage(sessionsCount: Int): String {
        return when {
            sessionsCount == 1 -> "Отличное начало! Первый шаг самый важный."
            sessionsCount < 5 -> "Ты на правильном пути! Продолжай практиковаться."
            sessionsCount < 10 -> "Твой прогресс впечатляет! Регулярность - ключ к успеху."
            sessionsCount < 30 -> "Ты уже опытный практик! Заметил изменения?"
            sessionsCount < 100 -> "Мастерство приходит с практикой. Ты молодец!"
            else -> "Ты настоящий мастер медитации! Вдохновляешь других своим примером."
        }
    }
}
