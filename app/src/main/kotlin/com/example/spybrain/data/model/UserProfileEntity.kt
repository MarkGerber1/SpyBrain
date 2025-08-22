package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.Profile
import java.util.Date

/**
 * Сущность профиля пользователя для хранения в базе данных.
 * @property id Идентификатор сущности.
 * @property userId Идентификатор пользователя.
 * @property name Имя пользователя.
 * @property email Email пользователя.
 * @property joinDate Дата присоединения.
 * @property streakDays Количество дней подряд.
 * @property avatarUrl URL аватара пользователя.
 * @property age Возраст пользователя (nullable).
 * @property gender Пол пользователя (nullable): "male", "female", "other".
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    /** Идентификатор сущности. */
    @PrimaryKey val id: Int = 0,
    /** Идентификатор пользователя. */
    val userId: String,
    /** Имя пользователя. */
    val name: String,
    /** Email пользователя. */
    val email: String,
    /** Дата присоединения. */
    val joinDate: Long,
    /** Количество дней подряд. */
    val streakDays: Int,
    /** URL аватара пользователя. */
    val avatarUrl: String?,
    /** Возраст пользователя. */
    val age: Int?,
    /** Пол пользователя. */
    val gender: String?
)

/**
 * Преобразует UserProfileEntity в доменную модель Profile.
 * @return Доменная модель Profile.
 */
fun UserProfileEntity.toDomain(): Profile = Profile(
    userId = userId,
    name = name,
    email = email,
    joinDate = Date(joinDate),
    streakDays = streakDays,
    avatarUrl = avatarUrl,
    age = age,
    gender = gender
)

/**
 * Преобразует Profile в UserProfileEntity.
 * @return Сущность базы данных.
 */
fun Profile.toEntity(): UserProfileEntity = UserProfileEntity(
    id = 0,
    userId = userId,
    name = name,
    email = email,
    joinDate = joinDate.time,
    streakDays = streakDays,
    avatarUrl = avatarUrl,
    age = age,
    gender = gender
)
