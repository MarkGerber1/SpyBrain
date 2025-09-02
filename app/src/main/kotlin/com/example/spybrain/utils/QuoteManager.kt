package com.example.spybrain.utils

import android.content.Context
import com.example.spybrain.R
import kotlin.random.Random

/**
 * Менеджер для управления мотивационными цитатами.
 * Обеспечивает показ разных цитат при каждом входе в приложение.
 */
object QuoteManager {
    
    /**
     * Получить случайную цитату.
     * Использует время в миллисекундах как seed для генератора случайных чисел,
     * что обеспечивает разные цитаты при каждом входе.
     */
    fun getRandomQuote(context: Context): String {
        val quotes = listOf(
            R.string.quote_meditation,
            R.string.quote_relaxation,
            R.string.quote_breathing,
            R.string.quote_calmness,
            R.string.quote_attention,
            R.string.quote_mindfulness,
            R.string.quote_peace,
            R.string.quote_moment,
            R.string.quote_breath_life,
            R.string.quote_silence,
            R.string.quote_balance,
            R.string.quote_inner_strength,
            R.string.quote_present,
            R.string.quote_thoughts,
            R.string.quote_journey,
            R.string.quote_gratitude,
            R.string.quote_flow,
            R.string.quote_heart,
            R.string.quote_wisdom,
            R.string.quote_freedom,
            R.string.quote_energy,
            R.string.quote_change,
            R.string.quote_compassion,
            R.string.quote_trust,
            R.string.quote_growth,
            R.string.quote_love,
            R.string.quote_patience,
            R.string.quote_dreams,
            R.string.quote_strength,
            R.string.quote_soul,
            R.string.quote_time,
            R.string.quote_hope,
            R.string.quote_nature,
            R.string.quote_purpose,
            R.string.quote_forgiveness,
            R.string.quote_courage,
            R.string.quote_mindful_eating,
            R.string.quote_water,
            R.string.quote_sunrise,
            R.string.quote_kindness,
            R.string.quote_meditation_deep,
            R.string.quote_abundance,
            R.string.quote_simplicity,
            R.string.quote_connection,
            R.string.quote_intuition,
            R.string.quote_surrender,
            R.string.quote_joy,
            R.string.quote_waves,
            R.string.quote_stars,
            R.string.quote_breath_power,
            R.string.quote_healing,
            R.string.quote_miracle,
            R.string.quote_faith,
            R.string.quote_wholeness,
            R.string.quote_sacred
        )
        
        // Используем текущее время как seed для обеспечения различных цитат
        val currentTime = System.currentTimeMillis()
        val random = Random(currentTime)
        val randomIndex = random.nextInt(quotes.size)
        
        return context.getString(quotes[randomIndex])
    }
}
