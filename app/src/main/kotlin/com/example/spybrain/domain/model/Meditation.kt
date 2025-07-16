package com.example.spybrain.domain.model

/**
 * Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ РјРµРґРёС‚Р°С†РёРё.
 */
data class Meditation(
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РјРµРґРёС‚Р°С†РёРё. */
    val id: String,
    /** РќР°Р·РІР°РЅРёРµ РјРµРґРёС‚Р°С†РёРё. */
    val title: String,
    /** РћРїРёСЃР°РЅРёРµ РјРµРґРёС‚Р°С†РёРё. */
    val description: String?,
    /** Р”Р»РёС‚РµР»СЊРЅРѕСЃС‚СЊ РІ РјРёРЅСѓС‚Р°С…. */
    val durationMinutes: Int,
    /** URL Р°СѓРґРёРѕС„Р°Р№Р»Р°. */
    val audioUrl: String?,
    /** РљР°С‚РµРіРѕСЂРёСЏ РјРµРґРёС‚Р°С†РёРё. */
    val category: String?
)
