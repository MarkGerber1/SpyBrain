package com.example.spybrain.presentation.meditation

import com.example.spybrain.domain.model.MeditationProgram
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.util.UiError

/**
 * РљРѕРЅС‚СЂР°РєС‚ MVI РґР»СЏ СЌРєСЂР°РЅР° Р±РёР±Р»РёРѕС‚РµРєРё РјРµРґРёС‚Р°С†РёР№.
 */
interface MeditationLibraryContract {
    /**
     * РЎРѕСЃС‚РѕСЏРЅРёРµ СЌРєСЂР°РЅР° Р±РёР±Р»РёРѕС‚РµРєРё РјРµРґРёС‚Р°С†РёР№.
     * @property programs РЎРїРёСЃРѕРє РїСЂРѕРіСЂР°РјРј РјРµРґРёС‚Р°С†РёР№.
     * @property error РћС€РёР±РєР° UI.
     */
    data class State(
        /** РЎРїРёСЃРѕРє РїСЂРѕРіСЂР°РјРј РјРµРґРёС‚Р°С†РёР№. */
        val programs: List<MeditationProgram> = emptyList(),
        /** РћС€РёР±РєР° UI. */
        val error: UiError? = null
    ) : UiState

    /**
     * РЎРѕР±С‹С‚РёСЏ СЌРєСЂР°РЅР° Р±РёР±Р»РёРѕС‚РµРєРё РјРµРґРёС‚Р°С†РёР№.
     */
    sealed class Event : UiEvent {
        /** Р—Р°РіСЂСѓР·РёС‚СЊ РїСЂРѕРіСЂР°РјРјС‹ РјРµРґРёС‚Р°С†РёР№. */
        object LoadPrograms : Event()
    }

    /**
     * Р­С„С„РµРєС‚С‹ СЌРєСЂР°РЅР° Р±РёР±Р»РёРѕС‚РµРєРё РјРµРґРёС‚Р°С†РёР№.
     */
    sealed class Effect : UiEffect {
        /** РџРѕРєР°Р· РѕС€РёР±РєРё. */
        data class ShowError(val error: UiError) : Effect()
    }
}
