package com.example.spybrain.domain.model

import java.util.Date

data class Profile(
    val userId: String,
    val name: String,
    val email: String, // Consider if email is needed/handled elsewhere
    val joinDate: Date,
    val streakDays: Int = 0,
    val avatarUrl: String? = null
) 