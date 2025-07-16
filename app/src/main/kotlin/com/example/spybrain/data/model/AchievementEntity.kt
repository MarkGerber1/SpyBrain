package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.Achievement

/**
 * РЎСѓС‰РЅРѕСЃС‚СЊ РґР»СЏ С…СЂР°РЅРµРЅРёСЏ РґРѕСЃС‚РёР¶РµРЅРёР№.
 * @property id РЈРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ.
 * @property title РќР°Р·РІР°РЅРёРµ РґРѕСЃС‚РёР¶РµРЅРёСЏ.
 * @property description РћРїРёСЃР°РЅРёРµ РґРѕСЃС‚РёР¶РµРЅРёСЏ.
 * @property isUnlocked РџСЂРёР·РЅР°Рє СЂР°Р·Р±Р»РѕРєРёСЂРѕРІРєРё.
 * @property unlockedAt Р’СЂРµРјСЏ СЂР°Р·Р±Р»РѕРєРёСЂРѕРІРєРё (timestamp).
 */
@Entity(tableName = "achievements")
data class AchievementEntity(
    /** РЈРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ. */
    @PrimaryKey val id: String,
    /** РќР°Р·РІР°РЅРёРµ РґРѕСЃС‚РёР¶РµРЅРёСЏ. */
    val title: String,
    /** РћРїРёСЃР°РЅРёРµ РґРѕСЃС‚РёР¶РµРЅРёСЏ. */
    val description: String,
    /** РџСЂРёР·РЅР°Рє СЂР°Р·Р±Р»РѕРєРёСЂРѕРІРєРё. */
    val isUnlocked: Boolean,
    /** Р’СЂРµРјСЏ СЂР°Р·Р±Р»РѕРєРёСЂРѕРІРєРё (timestamp). */
    val unlockedAt: Long?
)

/** РџСЂРµРѕР±СЂР°Р·РѕРІР°РЅРёРµ Room-СЃСѓС‰РЅРѕСЃС‚Рё РІ РґРѕРјРµРЅРЅСѓСЋ РјРѕРґРµР»СЊ. */
fun AchievementEntity.toDomain(): Achievement = Achievement(
    id = id,
    title = title,
    description = description,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt
)

/** РџСЂРµРѕР±СЂР°Р·РѕРІР°РЅРёРµ РґРѕРјРµРЅРЅРѕР№ РјРѕРґРµР»Рё РІ Room-СЃСѓС‰РЅРѕСЃС‚СЊ. */
fun Achievement.toEntity(): AchievementEntity = AchievementEntity(
    id = id,
    title = title,
    description = description,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt
)
