package com.example.spybrain.domain.model

import java.util.Date

/**
 * Профиль пользователя.
 * @property userId Идентификатор пользователя.
 * @property name Имя пользователя.
 * @property email Email пользователя.
 * @property joinDate Дата регистрации.
 * @property streakDays Количество дней в серии.
 * @property avatarUrl URL аватара пользователя.
 * @property age Возраст пользователя (необязательно).
 * @property gender Пол пользователя (необязательно): "male", "female", "other".
 */
data class Profile(
    val userId: String,
    val name: String,
    val email: String, // Consider if email is needed/handled elsewhere
    val joinDate: Date,
    val streakDays: Int = 0,
    val avatarUrl: String? = null,
    val age: Int? = null,
    val gender: String? = null
)
