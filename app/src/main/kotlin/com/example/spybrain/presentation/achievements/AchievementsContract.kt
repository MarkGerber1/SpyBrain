package com.example.spybrain.presentation.achievements

import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.domain.model.AchievementType
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.util.UiError

/**
 */
interface AchievementsContract {
    /**
     * @property selectedTab Р’С‹Р±СЂР°РЅРЅР°СЏ РІРєР»Р°РґРєР°.
     * @property totalPoints РћР±С‰РµРµ РєРѕР»РёС‡РµСЃС‚РІРѕ РѕС‡РєРѕРІ.
     * @property userLevel РЈСЂРѕРІРµРЅСЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    data class State(
        val achievements: List<Achievement> = emptyList(),
        val selectedTab: AchievementType = AchievementType.GENERAL,
        val totalPoints: Int = 0,
        val userLevel: UserLevel = UserLevel(1, 0, 100, 0f),
        val isLoading: Boolean = false,
    ) : UiState

    /**
     */
    sealed class Event : UiEvent {
        /**
         */
        object LoadAchievements : Event()
        /** Р’С‹Р±СЂР°РЅРЅС‹Р№ С‚РёРї РґРѕСЃС‚РёР¶РµРЅРёСЏ. */
        /**
         * РўРёРї РґРѕСЃС‚РёР¶РµРЅРёСЏ, РІС‹Р±СЂР°РЅРЅС‹Р№ РїРѕР»СЊР·РѕРІР°С‚РµР»РµРј.
         */
        data class TabSelected(
            /**
             * РўРёРї РґРѕСЃС‚РёР¶РµРЅРёСЏ.
             */
            val type: AchievementType
        ) : Event()
        /**
         */
        data class AchievementClicked(
            /**
             * Р”РѕСЃС‚РёР¶РµРЅРёРµ.
             */
            val achievement: Achievement
        ) : Event()
    }

    /**
     */
    sealed class Effect : UiEffect {
        /**
         */
        data class ShowError(
            /**
             * РћС€РёР±РєР°.
             */
            val error: UiError
        ) : Effect()
        /** РџРѕРєР°Р· РґРµС‚Р°Р»РµР№ РґРѕСЃС‚РёР¶РµРЅРёСЏ. */
        /**
         * Р”РѕСЃС‚РёР¶РµРЅРёРµ РґР»СЏ РїРѕРєР°Р·Р° РґРµС‚Р°Р»РµР№.
         */
        data class ShowAchievementDetails(
            /**
             * Р”РѕСЃС‚РёР¶РµРЅРёРµ.
             */
            val achievement: Achievement
        ) : Effect()
        /** РџРѕРєР°Р· РІСЃРїР»С‹РІР°СЋС‰РµРіРѕ СЃРѕРѕР±С‰РµРЅРёСЏ. */
        /**
         * РЎРѕРѕР±С‰РµРЅРёРµ РґР»СЏ РїРѕРєР°Р·Р°.
         */
        data class ShowToast(
            /**
             * РЎРѕРѕР±С‰РµРЅРёРµ.
             */
            val message: String
        ) : Effect()
    }

    /**
     * РњРѕРґРµР»СЊ СѓСЂРѕРІРЅСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @property level РўРµРєСѓС‰РёР№ СѓСЂРѕРІРµРЅСЊ.
     * @property currentPoints РўРµРєСѓС‰РёРµ РѕС‡РєРё.
     * @property requiredPoints РћС‡РєРё РґРѕ СЃР»РµРґСѓСЋС‰РµРіРѕ СѓСЂРѕРІРЅСЏ.
     * @property progress РџСЂРѕРіСЂРµСЃСЃ РґРѕ СЃР»РµРґСѓСЋС‰РµРіРѕ СѓСЂРѕРІРЅСЏ.
     */
    data class UserLevel(
        val level: Int,
        val currentPoints: Int,
        val requiredPoints: Int,
        val progress: Float
    )
}
