package com.example.spybrain.data.repository

import android.content.Context
import com.example.spybrain.R
import com.example.spybrain.domain.model.BreathingPattern
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с дыхательными паттернами.
 * Предоставляет стандартные шаблоны дыхания.
 */
@Singleton
class BreathingPatternRepository @Inject constructor(
    private val context: Context
) {

    /**
     * Получить все паттерны дыхания.
     * @return Список паттернов.
     */
    fun getAllPatterns(): List<BreathingPattern> = listOf(
        // Спокойное дыхание
        BreathingPattern(
            id = "classic_meditation",
            name = context.getString(R.string.breathing_pattern_calm),
            voicePrompt = context.getString(R.string.voice_prompt_calm),
            description = context.getString(R.string.breathing_pattern_calm_desc),
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 0,
            exhaleSeconds = 4,
            holdAfterExhaleSeconds = 0,
            totalCycles = 10
        ),

        // Квадратное дыхание
        BreathingPattern(
            id = "box_breathing",
            name = context.getString(R.string.breathing_pattern_box),
            voicePrompt = context.getString(R.string.voice_prompt_box),
            description = context.getString(R.string.breathing_pattern_box_desc),
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 4,
            exhaleSeconds = 4,
            holdAfterExhaleSeconds = 4,
            totalCycles = 8
        ),

        // Дыхание для сна
        BreathingPattern(
            id = "breathing_478",
            name = context.getString(R.string.breathing_pattern_sleep),
            voicePrompt = context.getString(R.string.voice_prompt_sleep),
            description = context.getString(R.string.breathing_pattern_sleep_desc),
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 7,
            exhaleSeconds = 8,
            holdAfterExhaleSeconds = 0,
            totalCycles = 6
        ),

        // Сила животного
        BreathingPattern(
            id = "lion_breath",
            name = context.getString(R.string.breathing_pattern_lion),
            voicePrompt = context.getString(R.string.voice_prompt_lion),
            description = context.getString(R.string.breathing_pattern_lion_desc),
            inhaleSeconds = 3,
            holdAfterInhaleSeconds = 0,
            exhaleSeconds = 6,
            holdAfterExhaleSeconds = 0,
            totalCycles = 5
        ),

        // Южанин желтый
        BreathingPattern(
            id = "bee_breath",
            name = context.getString(R.string.breathing_pattern_bee),
            voicePrompt = context.getString(R.string.voice_prompt_bee),
            description = context.getString(R.string.breathing_pattern_bee_desc),
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 0,
            exhaleSeconds = 6,
            holdAfterExhaleSeconds = 0,
            totalCycles = 7
        ),

        // Огненное дыхание
        BreathingPattern(
            id = "fire_breath",
            name = context.getString(R.string.breathing_pattern_fire),
            voicePrompt = context.getString(R.string.voice_prompt_fire),
            description = context.getString(R.string.breathing_pattern_fire_desc),
            inhaleSeconds = 1,
            holdAfterInhaleSeconds = 0,
            exhaleSeconds = 1,
            holdAfterExhaleSeconds = 0,
            totalCycles = 20
        ),

        // Вечернее дыхание
        BreathingPattern(
            id = "sleep_breathing",
            name = context.getString(R.string.breathing_pattern_evening),
            voicePrompt = context.getString(R.string.voice_prompt_evening),
            description = context.getString(R.string.breathing_pattern_evening_desc),
            inhaleSeconds = 5,
            holdAfterInhaleSeconds = 2,
            exhaleSeconds = 7,
            holdAfterExhaleSeconds = 1,
            totalCycles = 10
        ),

        // Ясносостая энергия
        BreathingPattern(
            id = "focus_breathing",
            name = context.getString(R.string.breathing_pattern_clarity),
            voicePrompt = context.getString(R.string.voice_prompt_clarity),
            description = context.getString(R.string.breathing_pattern_clarity_desc),
            inhaleSeconds = 3,
            holdAfterInhaleSeconds = 3,
            exhaleSeconds = 3,
            holdAfterExhaleSeconds = 3,
            totalCycles = 12
        ),

        // Утренец энергия
        BreathingPattern(
            id = "energy_breathing",
            name = context.getString(R.string.breathing_pattern_morning),
            voicePrompt = context.getString(R.string.voice_prompt_morning),
            description = context.getString(R.string.breathing_pattern_morning_desc),
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 2,
            exhaleSeconds = 4,
            holdAfterExhaleSeconds = 2,
            totalCycles = 8
        ),

        // Творческий поток
        BreathingPattern(
            id = "creative_breathing",
            name = context.getString(R.string.breathing_pattern_creative),
            voicePrompt = context.getString(R.string.voice_prompt_creative),
            description = context.getString(R.string.breathing_pattern_creative_desc),
            inhaleSeconds = 6,
            holdAfterInhaleSeconds = 4,
            exhaleSeconds = 6,
            holdAfterExhaleSeconds = 2,
            totalCycles = 6
        )
    )

    /**
     * Получить паттерн по идентификатору.
     * @param id Идентификатор паттерна.
     * @return Паттерн дыхания или null.
     */
    fun getPatternById(id: String): BreathingPattern? {
        return getAllPatterns().find { it.id == id }
    }

    /**
     * Получить паттерны по категории.
     * @param category Категория.
     * @return Список паттернов.
     */
    fun getPatternsByCategory(category: String): List<BreathingPattern> {
        return when (category) {
            "relaxation" -> listOf(
                getPatternById("classic_meditation")!!,
                getPatternById("breathing_478")!!,
                getPatternById("sleep_breathing")!!
            )
            "energy" -> listOf(
                getPatternById("fire_breath")!!,
                getPatternById("energy_breathing")!!,
                getPatternById("lion_breath")!!
            )
            "focus" -> listOf(
                getPatternById("box_breathing")!!,
                getPatternById("focus_breathing")!!,
                getPatternById("creative_breathing")!!
            )
            else -> getAllPatterns()
        }
    }
}
