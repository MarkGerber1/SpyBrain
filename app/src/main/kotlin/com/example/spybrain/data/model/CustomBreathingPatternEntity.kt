package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.CustomBreathingPattern

/**
 * РЎСѓС‰РЅРѕСЃС‚СЊ РґР»СЏ С…СЂР°РЅРµРЅРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЃРєРёС… РїР°С‚С‚РµСЂРЅРѕРІ РґС‹С…Р°РЅРёСЏ.
 * @property id РЈРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ.
 * @property name РќР°Р·РІР°РЅРёРµ РїР°С‚С‚РµСЂРЅР°.
 * @property description РћРїРёСЃР°РЅРёРµ РїР°С‚С‚РµСЂРЅР°.
 * @property inhaleSeconds Р’СЂРµРјСЏ РІРґРѕС…Р° (СЃРµРєСѓРЅРґС‹).
 * @property holdAfterInhaleSeconds Р—Р°РґРµСЂР¶РєР° РїРѕСЃР»Рµ РІРґРѕС…Р° (СЃРµРєСѓРЅРґС‹).
 * @property exhaleSeconds Р’СЂРµРјСЏ РІС‹РґРѕС…Р° (СЃРµРєСѓРЅРґС‹).
 * @property holdAfterExhaleSeconds Р—Р°РґРµСЂР¶РєР° РїРѕСЃР»Рµ РІС‹РґРѕС…Р° (СЃРµРєСѓРЅРґС‹).
 * @property totalCycles РљРѕР»РёС‡РµСЃС‚РІРѕ С†РёРєР»РѕРІ.
 */
@Entity(tableName = "custom_breathing_patterns")
data class CustomBreathingPatternEntity(
    /** РЈРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ. */
    @PrimaryKey val id: String,
    /** РќР°Р·РІР°РЅРёРµ РїР°С‚С‚РµСЂРЅР°. */
    val name: String,
    /** РћРїРёСЃР°РЅРёРµ РїР°С‚С‚РµСЂРЅР°. */
    val description: String?,
    /** Р’СЂРµРјСЏ РІРґРѕС…Р° (СЃРµРєСѓРЅРґС‹). */
    val inhaleSeconds: Int,
    /** Р—Р°РґРµСЂР¶РєР° РїРѕСЃР»Рµ РІРґРѕС…Р° (СЃРµРєСѓРЅРґС‹). */
    val holdAfterInhaleSeconds: Int,
    /** Р’СЂРµРјСЏ РІС‹РґРѕС…Р° (СЃРµРєСѓРЅРґС‹). */
    val exhaleSeconds: Int,
    /** Р—Р°РґРµСЂР¶РєР° РїРѕСЃР»Рµ РІС‹РґРѕС…Р° (СЃРµРєСѓРЅРґС‹). */
    val holdAfterExhaleSeconds: Int,
    /** РљРѕР»РёС‡РµСЃС‚РІРѕ С†РёРєР»РѕРІ. */
    val totalCycles: Int
)

/**
 * РџСЂРµРѕР±СЂР°Р·СѓРµС‚ CustomBreathingPatternEntity РІ РґРѕРјРµРЅРЅСѓСЋ РјРѕРґРµР»СЊ.
 * @return Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ CustomBreathingPattern.
 */
fun CustomBreathingPatternEntity.toDomain(): CustomBreathingPattern = CustomBreathingPattern(
    id = id.toLongOrNull() ?: 0L,
    name = name,
    description = description,
    inhaleSeconds = inhaleSeconds,
    holdAfterInhaleSeconds = holdAfterInhaleSeconds,
    exhaleSeconds = exhaleSeconds,
    holdAfterExhaleSeconds = holdAfterExhaleSeconds,
    totalCycles = totalCycles
)

/**
 * РџСЂРµРѕР±СЂР°Р·СѓРµС‚ CustomBreathingPattern РІ CustomBreathingPatternEntity.
 * @return РЎСѓС‰РЅРѕСЃС‚СЊ Р±Р°Р·С‹ РґР°РЅРЅС‹С….
 */
fun CustomBreathingPattern.toEntity(): CustomBreathingPatternEntity = CustomBreathingPatternEntity(
    id = id.toString(),
    name = name,
    description = description,
    inhaleSeconds = inhaleSeconds,
    holdAfterInhaleSeconds = holdAfterInhaleSeconds,
    exhaleSeconds = exhaleSeconds,
    holdAfterExhaleSeconds = holdAfterExhaleSeconds,
    totalCycles = totalCycles
)
