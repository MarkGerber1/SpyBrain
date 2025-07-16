package com.example.spybrain.domain.model

import androidx.annotation.DrawableRes

/**
 * Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ РїСЂРѕРіСЂР°РјРјС‹ РјРµРґРёС‚Р°С†РёРё.
 */
data class MeditationProgram(
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїСЂРѕРіСЂР°РјРјС‹. */
    val id: String,
    /** РќР°Р·РІР°РЅРёРµ РїСЂРѕРіСЂР°РјРјС‹. */
    val title: String,
    /** РћРїРёСЃР°РЅРёРµ РїСЂРѕРіСЂР°РјРјС‹. */
    val description: String?,
    /** URL Р°СѓРґРёРѕС„Р°Р№Р»Р°. */
    val audioUrl: String?,
    /** Р РµСЃСѓСЂСЃ С„РѕРЅР°. */
    val backgroundResId: Int?,
    /** Р РµСЃСѓСЂСЃ РёРєРѕРЅРєРё. */
    val iconResId: Int?
)
