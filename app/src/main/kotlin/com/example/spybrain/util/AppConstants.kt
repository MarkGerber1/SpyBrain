package com.example.spybrain.util

import android.content.Context
import com.example.spybrain.R

object AppConstants {
    fun getWeekdays(context: Context): List<String> = listOf(
        context.getString(R.string.weekday_monday),
        context.getString(R.string.weekday_tuesday),
        context.getString(R.string.weekday_wednesday),
        context.getString(R.string.weekday_thursday),
        context.getString(R.string.weekday_friday),
        context.getString(R.string.weekday_saturday),
        context.getString(R.string.weekday_sunday)
    )
} 