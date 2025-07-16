package com.example.spybrain.presentation.breathing.patternbuilder

import com.example.spybrain.domain.model.CustomBreathingPattern
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.util.UiError

/**
 * РљРѕРЅС‚СЂР°РєС‚ MVI РґР»СЏ РєРѕРЅСЃС‚СЂСѓРєС‚РѕСЂР° РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
 */
object BreathingPatternBuilderContract {
    /**
     * РЎРѕСЃС‚РѕСЏРЅРёРµ СЌРєСЂР°РЅР° РєРѕРЅСЃС‚СЂСѓРєС‚РѕСЂР° РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
     * @property id РЈРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ.
     * @property patterns РЎРїРёСЃРѕРє РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёС… РїР°С‚С‚РµСЂРЅРѕРІ.
     * @property name РќР°Р·РІР°РЅРёРµ С€Р°Р±Р»РѕРЅР°.
     * @property description РћРїРёСЃР°РЅРёРµ С€Р°Р±Р»РѕРЅР°.
     * @property inhaleSeconds Р’СЂРµРјСЏ РІРґРѕС…Р° (СЃРµРє).
     * @property holdAfterInhaleSeconds Р—Р°РґРµСЂР¶РєР° РїРѕСЃР»Рµ РІРґРѕС…Р° (СЃРµРє).
     * @property exhaleSeconds Р’СЂРµРјСЏ РІС‹РґРѕС…Р° (СЃРµРє).
     * @property holdAfterExhaleSeconds Р—Р°РґРµСЂР¶РєР° РїРѕСЃР»Рµ РІС‹РґРѕС…Р° (СЃРµРє).
     * @property totalCycles РљРѕР»РёС‡РµСЃС‚РІРѕ С†РёРєР»РѕРІ.
     * @property isLoading РџСЂРёР·РЅР°Рє Р·Р°РіСЂСѓР·РєРё.
     * @property error РћС€РёР±РєР° UI.
     */
    data class State(
        /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ С€Р°Р±Р»РѕРЅР°. */
        val id: Long? = null,
        /** РЎРїРёСЃРѕРє РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёС… РїР°С‚С‚РµСЂРЅРѕРІ. */
        val patterns: List<CustomBreathingPattern> = emptyList(),
        /** РќР°Р·РІР°РЅРёРµ С€Р°Р±Р»РѕРЅР°. */
        val name: String = "",
        /** РћРїРёСЃР°РЅРёРµ С€Р°Р±Р»РѕРЅР°. */
        val description: String = "",
        /** Р’СЂРµРјСЏ РІРґРѕС…Р° (СЃРµРє). */
        val inhaleSeconds: String = "",
        /** Р—Р°РґРµСЂР¶РєР° РїРѕСЃР»Рµ РІРґРѕС…Р° (СЃРµРє). */
        val holdAfterInhaleSeconds: String = "",
        /** Р’СЂРµРјСЏ РІС‹РґРѕС…Р° (СЃРµРє). */
        val exhaleSeconds: String = "",
        /** Р—Р°РґРµСЂР¶РєР° РїРѕСЃР»Рµ РІС‹РґРѕС…Р° (СЃРµРє). */
        val holdAfterExhaleSeconds: String = "",
        /** РљРѕР»РёС‡РµСЃС‚РІРѕ С†РёРєР»РѕРІ. */
        val totalCycles: String = "",
        /** РџСЂРёР·РЅР°Рє Р·Р°РіСЂСѓР·РєРё. */
        val isLoading: Boolean = false,
        /** РћС€РёР±РєР° UI. */
        val error: UiError? = null
    ) : UiState

    /**
     * РЎРѕР±С‹С‚РёСЏ СЌРєСЂР°РЅР° РєРѕРЅСЃС‚СЂСѓРєС‚РѕСЂР° РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
     */
    sealed class Event : UiEvent {
        /** Р—Р°РіСЂСѓР·РёС‚СЊ СЃСўСўРІСѓСЋС‰РёР№ С€Р°Р±Р»РѕРЅ РїРѕ ID. */
        data class LoadPattern(val id: Long) : Event()
        /** Р’РІРѕРґ РЅР°Р·РІР°РЅРёСЏ. */
        data class EnterName(val name: String) : Event()
        /** Р’РІРѕРґ РѕРїРёСЃР°РЅРёСЏ. */
        data class EnterDescription(val description: String) : Event()
        /** Р’РІРѕРґ РІСЂРµРјРµРЅРё РІРґРѕС…Р°. */
        data class EnterInhale(val seconds: String) : Event()
        /** Р’РІРѕРґ Р·Р°РґРµСЂР¶РєРё РїРѕСЃР»Рµ РІРґРѕС…Р°. */
        data class EnterHoldInhale(val seconds: String) : Event()
        /** Р’РІРѕРґ РІСЂРµРјРµРЅРё РІС‹РґРѕС…Р°. */
        data class EnterExhale(val seconds: String) : Event()
        /** Р’РІРѕРґ Р·Р°РґРµСЂР¶РєРё РїРѕСЃР»Рµ РІС‹РґРѕС…Р°. */
        data class EnterHoldExhale(val seconds: String) : Event()
        /** Р’РІРѕРґ РєРѕР»РёС‡РµСЃС‚РІР° С†РёРєР»РѕРІ. */
        data class EnterCycles(val cycles: String) : Event()
        /** РЎРѕС…СЂР°РЅРёС‚СЊ С€Р°Р±Р»РѕРЅ. */
        object SavePattern : Event()
        /** РЈРґР°Р»РёС‚СЊ С€Р°Р±Р»РѕРЅ. */
        data class DeletePattern(val pattern: CustomBreathingPattern) : Event()
    }

    /**
     * Р­С„С„РµРєС‚С‹ СЌРєСЂР°РЅР° РєРѕРЅСЃС‚СЂСѓРєС‚РѕСЂР° РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
     */
    sealed class Effect : UiEffect {
        /** РџРѕРєР°Р· РѕС€РёР±РєРё. */
        data class ShowError(val error: UiError) : Effect()
        /** РџРѕРєР°Р· СЃРѕРѕР±С‰РµРЅРёСЏ РѕР± СўСЃРїРµС…Рµ. */
        data class ShowSuccessMessage(val message: String) : Effect()
    }
}
