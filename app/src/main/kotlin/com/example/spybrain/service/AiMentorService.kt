package com.example.spybrain.service

import javax.inject.Inject
import javax.inject.Singleton
import com.example.spybrain.presentation.breathing.BreathingContract
import com.example.spybrain.domain.service.IAiMentor

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
        "Отпускай мысли и возвращайся к дыханию."
    )

    override fun giveBreathingAdvice(phase: Any) {
        if (phase is BreathingContract.BreathingPhase) {
            val advice = when (phase) {
                BreathingContract.BreathingPhase.Inhale -> inhaleAdvices.random()
                BreathingContract.BreathingPhase.HoldAfterInhale -> holdAfterInhaleAdvices.random()
                BreathingContract.BreathingPhase.Exhale -> exhaleAdvices.random()
                BreathingContract.BreathingPhase.HoldAfterExhale -> holdAfterExhaleAdvices.random()
                else -> null
            }
            advice?.let { voiceAssistantService.speak(it) }
        } else {
            android.util.Log.d("AiMentorService", "Not implemented for this phase type")
        }
    }

    override fun giveMeditationAdvice() {
        val advice = meditationAdvices.random()
        voiceAssistantService.speak(advice)
    }
} 