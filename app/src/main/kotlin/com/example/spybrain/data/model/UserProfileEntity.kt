package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.Profile
import java.util.Date

/**
 * РЎСѓС‰РЅРѕСЃС‚СЊ РїСЂРѕС„РёР»СЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ РґР»СЏ С…СЂР°РЅРµРЅРёСЏ РІ Р±Р°Р·Рµ РґР°РЅРЅС‹С….
 * @property id РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ СЃСѓС‰РЅРѕСЃС‚Рё.
 * @property userId РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 * @property name РРјСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 * @property email Email РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 * @property joinDate Р”Р°С‚Р° РїСЂРёСЃРѕРµРґРёРЅРµРЅРёСЏ.
 * @property streakDays РљРѕР»РёС‡РµСЃС‚РІРѕ РґРЅРµР№ РїРѕРґСЂСЏРґ.
 * @property avatarUrl URL Р°РІР°С‚Р°СЂР° РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ СЃСѓС‰РЅРѕСЃС‚Рё. */
    @PrimaryKey val id: Int = 0,
    /** РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ. */
    val userId: String,
    /** РРјСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ. */
    val name: String,
    /** Email РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ. */
    val email: String,
    /** Р”Р°С‚Р° РїСЂРёСЃРѕРµРґРёРЅРµРЅРёСЏ. */
    val joinDate: Long,
    /** РљРѕР»РёС‡РµСЃС‚РІРѕ РґРЅРµР№ РїРѕРґСЂСЏРґ. */
    val streakDays: Int,
    /** URL Р°РІР°С‚Р°СЂР° РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ. */
    val avatarUrl: String?
)

/**
 * РџСЂРµРѕР±СЂР°Р·СѓРµС‚ UserProfileEntity РІ РґРѕРјРµРЅРЅСѓСЋ РјРѕРґРµР»СЊ Profile.
 * @return Р”РѕРјРµРЅРЅР°СЏ РјРѕРґРµР»СЊ Profile.
 */
fun UserProfileEntity.toDomain(): Profile = Profile(
    userId = userId,
    name = name,
    email = email,
    joinDate = Date(joinDate),
    streakDays = streakDays,
    avatarUrl = avatarUrl
)

/**
 * РџСЂРµРѕР±СЂР°Р·СѓРµС‚ Profile РІ UserProfileEntity.
 * @return РЎСѓС‰РЅРѕСЃС‚СЊ Р±Р°Р·С‹ РґР°РЅРЅС‹С….
 */
fun Profile.toEntity(): UserProfileEntity = UserProfileEntity(
    id = 0,
    userId = userId,
    name = name,
    email = email,
    joinDate = joinDate.time,
    streakDays = streakDays,
    avatarUrl = avatarUrl
)
