package com.example.spybrain.domain.model

import java.time.LocalDateTime

data class UserProfile(
    val id: String = "default_user",
    val name: String = "",
    val email: String = "",
    val avatarUri: String? = null,
    val totalPoints: Int = 0,
    val userLevel: Int = 1,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastActiveAt: LocalDateTime = LocalDateTime.now(),
    val settings: Map<String, String> = emptyMap()
) 