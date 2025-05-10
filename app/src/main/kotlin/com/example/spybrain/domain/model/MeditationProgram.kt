package com.example.spybrain.domain.model

import androidx.annotation.DrawableRes

/**
 * Модель тематической медитационной программы.
 */
data class MeditationProgram(
    val id: String,
    val title: String,
    val description: String,
    val audioUrl: String,
    @DrawableRes val backgroundResId: Int,
    @DrawableRes val iconResId: Int
) 