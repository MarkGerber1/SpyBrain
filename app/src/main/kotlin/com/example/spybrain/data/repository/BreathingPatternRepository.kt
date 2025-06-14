package com.example.spybrain.data.repository

import com.example.spybrain.domain.model.BreathingPattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreathingPatternRepository @Inject constructor() {
    
    fun getAllPatterns(): List<BreathingPattern> = listOf(
        // Классическая медитация
        BreathingPattern(
            id = "classic_meditation",
            name = "Классическая медитация",
            voicePrompt = "Дышите спокойно и глубоко. Вдохните через нос, выдохните через рот.",
            description = "Простая и эффективная техника для начинающих",
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 0,
            exhaleSeconds = 4,
            holdAfterExhaleSeconds = 0,
            totalCycles = 10
        ),
        
        // Квадратное дыхание
        BreathingPattern(
            id = "box_breathing",
            name = "Квадратное дыхание",
            voicePrompt = "Представьте квадрат. Вдохните на 4 счета, задержите на 4, выдохните на 4, задержите на 4.",
            description = "Техника для снятия стресса и улучшения концентрации",
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 4,
            exhaleSeconds = 4,
            holdAfterExhaleSeconds = 4,
            totalCycles = 8
        ),
        
        // Дыхание 4-7-8
        BreathingPattern(
            id = "breathing_478",
            name = "Дыхание 4-7-8",
            voicePrompt = "Вдохните на 4 счета, задержите дыхание на 7, выдохните на 8 счетов.",
            description = "Техника для быстрого засыпания и расслабления",
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 7,
            exhaleSeconds = 8,
            holdAfterExhaleSeconds = 0,
            totalCycles = 6
        ),
        
        // Дыхание льва
        BreathingPattern(
            id = "lion_breath",
            name = "Дыхание льва",
            voicePrompt = "Глубоко вдохните, затем выдохните с открытым ртом и высунутым языком, издавая звук 'ха'.",
            description = "Энергетизирующая техника для снятия напряжения в лице",
            inhaleSeconds = 3,
            holdAfterInhaleSeconds = 0,
            exhaleSeconds = 6,
            holdAfterExhaleSeconds = 0,
            totalCycles = 5
        ),
        
        // Дыхание пчелы
        BreathingPattern(
            id = "bee_breath",
            name = "Дыхание пчелы",
            voicePrompt = "Закройте уши пальцами, вдохните глубоко, на выдохе издавайте жужжащий звук.",
            description = "Успокаивающая техника для снятия тревоги",
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 0,
            exhaleSeconds = 6,
            holdAfterExhaleSeconds = 0,
            totalCycles = 7
        ),
        
        // Дыхание огня
        BreathingPattern(
            id = "fire_breath",
            name = "Дыхание огня",
            voicePrompt = "Быстрые и ритмичные вдохи и выдохи через нос, как будто вы дышите огнем.",
            description = "Энергетизирующая техника для пробуждения и очищения",
            inhaleSeconds = 1,
            holdAfterInhaleSeconds = 0,
            exhaleSeconds = 1,
            holdAfterExhaleSeconds = 0,
            totalCycles = 20
        ),
        
        // Дыхание для сна
        BreathingPattern(
            id = "sleep_breathing",
            name = "Дыхание для сна",
            voicePrompt = "Медленно и глубоко дышите, представляя, как с каждым выдохом уходит напряжение.",
            description = "Специальная техника для улучшения качества сна",
            inhaleSeconds = 5,
            holdAfterInhaleSeconds = 2,
            exhaleSeconds = 7,
            holdAfterExhaleSeconds = 1,
            totalCycles = 10
        ),
        
        // Дыхание для концентрации
        BreathingPattern(
            id = "focus_breathing",
            name = "Дыхание для концентрации",
            voicePrompt = "Сосредоточьтесь на дыхании. Вдохните ясность, выдохните отвлечения.",
            description = "Техника для улучшения концентрации и ясности ума",
            inhaleSeconds = 3,
            holdAfterInhaleSeconds = 3,
            exhaleSeconds = 3,
            holdAfterExhaleSeconds = 3,
            totalCycles = 12
        ),
        
        // Дыхание для энергии
        BreathingPattern(
            id = "energy_breathing",
            name = "Дыхание для энергии",
            voicePrompt = "Вдохните энергию и жизненную силу, выдохните усталость и слабость.",
            description = "Техника для повышения энергии и бодрости",
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 2,
            exhaleSeconds = 4,
            holdAfterExhaleSeconds = 2,
            totalCycles = 8
        ),
        
        // Дыхание для творчества
        BreathingPattern(
            id = "creative_breathing",
            name = "Дыхание для творчества",
            voicePrompt = "Вдохните вдохновение, выдохните сомнения. Откройте поток творчества.",
            description = "Техника для пробуждения творческого потенциала",
            inhaleSeconds = 6,
            holdAfterInhaleSeconds = 4,
            exhaleSeconds = 6,
            holdAfterExhaleSeconds = 2,
            totalCycles = 6
        )
    )
    
    fun getPatternById(id: String): BreathingPattern? {
        return getAllPatterns().find { it.id == id }
    }
    
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