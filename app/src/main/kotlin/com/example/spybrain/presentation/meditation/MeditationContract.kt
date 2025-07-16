package com.example.spybrain.presentation.meditation

import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.presentation.base.UiState
import com.example.spybrain.presentation.base.UiEvent
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.util.UiError
import com.example.spybrain.data.repository.MeditationRepositoryImpl.MeditationTrack

/**
 * РљРѕРЅС‚СЂР°РєС‚ MVI РґР»СЏ СЌРєСЂР°РЅР° РјРµРґРёС‚Р°С†РёРё.
 */
interface MeditationContract {
    /**
     * РЎРѕСЃС‚РѕСЏРЅРёРµ СЌРєСЂР°РЅР° РјРµРґРёС‚Р°С†РёРё.
     * @property isLoading РџСЂРёР·РЅР°Рє Р·Р°РіСЂСѓР·РєРё.
     * @property meditations РЎРїРёСЃРѕРє РјРµРґРёС‚Р°С†РёР№.
     * @property currentPlaying РўРµРєСѓС‰Р°СЏ РІРѕСЃРїСЂРѕРёР·РІРѕРґРёРјР°СЏ РјРµРґРёС‚Р°С†РёСЏ.
     * @property meditationTracks РЎРїРёСЃРѕРє С‚СЂРµРєРѕРІ РјРµРґРёС‚Р°С†РёРё.
     * @property selectedTrack Р’С‹Р±СЂР°РЅРЅС‹Р№ С‚СЂРµРє.
     * @property currentPlayingTrack РўРµРєСѓС‰РёР№ РІРѕСЃРїСЂРѕРёР·РІРѕРґРёРјС‹Р№ С‚СЂРµРє.
     * @property isTrackPlaying РџСЂРёР·РЅР°Рє РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёСЏ С‚СЂРµРєР°.
     * @property trackProgress РџСЂРѕРіСЂРµСЃСЃ С‚СЂРµРєР°.
     * @property trackDuration Р”Р»РёС‚РµР»СЊРЅРѕСЃС‚СЊ С‚СЂРµРєР°.
     * @property currentPosition РўРµРєСѓС‰Р°СЏ РїРѕР·РёС†РёСЏ С‚СЂРµРєР°.
     * @property error РћС€РёР±РєР° UI.
     */
    data class State(
        /** РџСЂРёР·РЅР°Рє Р·Р°РіСЂСѓР·РєРё. */
        val isLoading: Boolean = false,
        /** РЎРїРёСЃРѕРє РјРµРґРёС‚Р°С†РёР№. */
        val meditations: List<Meditation> = emptyList(),
        /** РўРµРєСѓС‰Р°СЏ РІРѕСЃРїСЂРѕРёР·РІРѕРґРёРјР°СЏ РјРµРґРёС‚Р°С†РёСЏ. */
        val currentPlaying: Meditation? = null,
        /** РЎРїРёСЃРѕРє С‚СЂРµРєРѕРІ РјРµРґРёС‚Р°С†РёРё. */
        val meditationTracks: List<MeditationTrack> = emptyList(),
        /** Р’С‹Р±СЂР°РЅРЅС‹Р№ С‚СЂРµРє. */
        val selectedTrack: MeditationTrack? = null,
        /** РўРµРєСѓС‰РёР№ РІРѕСЃРїСЂРѕРёР·РІРѕРґРёРјС‹Р№ С‚СЂРµРє. */
        val currentPlayingTrack: MeditationTrack? = null,
        /** РџСЂРёР·РЅР°Рє РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёСЏ С‚СЂРµРєР°. */
        val isTrackPlaying: Boolean = false,
        /** РџСЂРѕРіСЂРµСЃСЃ С‚СЂРµРєР°. */
        val trackProgress: Float = 0f,
        /** Р”Р»РёС‚РµР»СЊРЅРѕСЃС‚СЊ С‚СЂРµРєР°. */
        val trackDuration: Long = 0L,
        /** РўРµРєСѓС‰Р°СЏ РїРѕР·РёС†РёСЏ С‚СЂРµРєР°. */
        val currentPosition: Long = 0L,
        /** РћС€РёР±РєР° UI. */
        val error: UiError? = null // TODO СЂРµР°Р»РёР·РѕРІР°РЅРѕ: С†РµРЅС‚СЂР°Р»РёР·РѕРІР°РЅРЅР°СЏ РѕР±СЂР°Р±РѕС‚РєР° РѕС€РёР±РѕРє
    ) : UiState

    /**
     * РЎРѕР±С‹С‚РёСЏ СЌРєСЂР°РЅР° РјРµРґРёС‚Р°С†РёРё.
     */
    sealed class Event : UiEvent {
        /** Р—Р°РіСЂСѓР·РёС‚СЊ РјРµРґРёС‚Р°С†РёРё. */
        object LoadMeditations : Event()
        /** Р’РѕСЃРїСЂРѕРёР·РІРµСЃС‚Рё РјРµРґРёС‚Р°С†РёСЋ. */
        data class PlayMeditation(val meditation: Meditation) : Event()
        /** РџР°СѓР·Р° РјРµРґРёС‚Р°С†РёРё. */
        object PauseMeditation : Event()
        /** РћСЃС‚Р°РЅРѕРІРёС‚СЊ РјРµРґРёС‚Р°С†РёСЋ. */
        object StopMeditation : Event()
        /** Р“РѕР»РѕСЃРѕРІР°СЏ РєРѕРјР°РЅРґР°. */
        data class VoiceCommand(val command: String) : Event()
        /** Р—Р°РіСЂСѓР·РёС‚СЊ С‚СЂРµРєРё РјРµРґРёС‚Р°С†РёРё. */
        object LoadMeditationTracks : Event()
        /** Р’С‹Р±СЂР°С‚СЊ С‚СЂРµРє. */
        data class SelectMeditationTrack(val track: MeditationTrack) : Event()
        /** Р’РѕСЃРїСЂРѕРёР·РІРµСЃС‚Рё РІС‹Р±СЂР°РЅРЅС‹Р№ С‚СЂРµРє. */
        object PlaySelectedTrack : Event()
        /** РЎР»РµРґСѓСЋС‰РёР№ С‚СЂРµРє. */
        object NextTrack : Event()
        /** РџСЂРµРґС‹РґСѓС‰РёР№ С‚СЂРµРє. */
        object PreviousTrack : Event()
        /** РџРµСЂРµРјРѕС‚РєР° Рє РїРѕР·РёС†РёРё. */
        data class SeekToPosition(val position: Long) : Event()
    }

    /**
     * Р­С„С„РµРєС‚С‹ СЌРєСЂР°РЅР° РјРµРґРёС‚Р°С†РёРё.
     */
    sealed class Effect : UiEffect {
        /** РџРѕРєР°Р· РѕС€РёР±РєРё. */
        data class ShowError(val error: UiError) : Effect()
        /** Р“РѕР»РѕСЃРѕРІР°СЏ РїРѕРґСЃРєР°Р·РєР°. */
        data class Speak(val text: String) : Effect()
        /** РўСЂРµРє Р·Р°РїСѓС‰РµРЅ. */
        data class TrackStarted(val track: MeditationTrack) : Effect()
        /** РўСЂРµРє Р·Р°РІРµСЂС‘РЅ. */
        data class TrackCompleted(val track: MeditationTrack) : Effect()
    }
}
