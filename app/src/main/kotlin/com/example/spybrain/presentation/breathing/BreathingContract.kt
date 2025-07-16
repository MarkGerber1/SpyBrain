package com.example.spybrain.presentation.breathing

import com.example.spybrain.domain.model.BreathingPattern
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.util.UiError

/**
 * РљРѕРЅС‚СЂР°РєС‚ РґР»СЏ СЌРєСЂР°РЅР° РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїСЂР°РєС‚РёРє.
 */
object BreathingContract {
    /**
     * РЎРѕСЃС‚РѕСЏРЅРёРµ СЌРєСЂР°РЅР° РґС‹С…Р°РЅРёСЏ.
     * @property isLoading РџСЂРёР·РЅР°Рє Р·Р°РіСЂСѓР·РєРё.
     * @property patterns РЎРїРёСЃРѕРє РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
     * @property currentPattern РўРµРєСѓС‰РёР№ РІС‹Р±СЂР°РЅРЅС‹Р№ РїР°С‚С‚РµСЂРЅ.
     * @property currentPhase РўРµРєСѓС‰Р°СЏ С„Р°Р·Р° РґС‹С…Р°РЅРёСЏ.
     * @property cycleProgress РџСЂРѕРіСЂРµСЃСЃ С‚РµРєСѓС‰РµРіРѕ С†РёРєР»Р°.
     * @property remainingCycles РћСЃС‚Р°РІС€РёРµСЏ С†РёРєР»С‹.
     * @property error РћСЃС€РёР±РєР° UI.
     */
    data class State(
        val isLoading: Boolean = false,
        val patterns: List<BreathingPattern> = emptyList(),
        val currentPattern: BreathingPattern? = null,
        val currentPhase: BreathingPhase = BreathingPhase.Idle,
        val cycleProgress: Float = 0f, // 0.0 to 1.0 within a phase
        val remainingCycles: Int = 0,
    ) : UiState

    /**
     * РЎРѕР±С‹С‚РёСЏ СЌРєСЂР°РЅР° РґС‹С…Р°РЅРёСЏ.
     */
    sealed class Event : UiEvent {
        /** Р—Р°РіСЂСѓР·РєР° РїР°С‚С‚РµСЂРЅРѕРІ. */
        object LoadPatterns : Event()
        /** Р—Р°РїСѓСЃРє РїР°С‚С‚РµСЂРЅР°. */
        data class StartPattern(
            /** РџР°С‚С‚РµСЂРЅ РґС‹С…Р°РЅРёСЏ. */
            val pattern: BreathingPattern
        ) : Event()
        /** РћСЃС‚Р°РЅРѕРІРєР° РїР°С‚С‚РµСЂРЅР°. */
        object StopPattern : Event()
        /** Р“РѕР»РѕСЃРѕРІР°СЏ РєРѕРјР°РЅРґР°. */
        data class VoiceCommand(
            /** РўРµРєСЃС‚ РєРѕРјР°РЅРґС‹. */
            val text: String
        ) : Event()
        // Internal events can be added for timer ticks if needed
    }

    /**
     * Р­С„С„РµРєС‚С‹ СЌРєСЂР°РЅР° РґС‹С…Р°РЅРёСЏ.
     */
    sealed class Effect : UiEffect {
        /**
         * РџРѕРєР°Р· РѕС€РёР±РєРё.
         * @property error РћСЃС€РёР±РєР° UI.
         */
        data class ShowError(val error: UiError) : Effect()
        /** Р’РёР±СЂР°С†РёСЏ РїРё СЃРјРµРЅРµ С„Р°Р·С‹. */
        object Vibrate : Effect() // Example effect for phase change
        /**
         * Р“РѕР»РѕСЃРѕРІР°СЏ РїРѕРґСЃРєР°Р·РєР°.
         * @property text РўРµРєСЃС‚ РїРѕРґСЃРєР°Р·РєРё.
         */
        data class Speak(val text: String) : Effect() // Р“РѕР»РѕСЃРѕРІР°СЏ РїРѕРґСЃРєР°Р·РєР°
    }

    /**
     * Р¤Р°Р·С‹ РґС‹С…Р°С‚РµР»СЊРЅРѕРіРѕ С†РёРєР»Р°.
     */
    enum class BreathingPhase {
        Idle, Inhale, HoldAfterInhale, Exhale, HoldAfterExhale
    }
}
