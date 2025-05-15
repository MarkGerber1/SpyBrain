package com.example.spybrain.presentation.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.spybrain.R
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Перечисление для времени суток
 */
enum class TimeOfDay(val id: String) {
    MORNING("morning"),
    DAY("day"),
    EVENING("evening"),
    NIGHT("night");
    
    companion object {
        fun fromHour(hour: Int): TimeOfDay = when (hour) {
            in 5..11 -> MORNING
            in 12..16 -> DAY
            in 17..21 -> EVENING
            else -> NIGHT
        }
    }
}

/**
 * Данные о фоне для определенного времени суток
 */
data class TimeBasedBackground(
    val backgroundResId: Int,
    val gradientStart: Color,
    val gradientEnd: Color,
    val overlayStart: Color = Color.Black.copy(alpha = 0.5f),
    val overlayEnd: Color = Color.Black.copy(alpha = 0.1f),
    val isDark: Boolean = false
)

/**
 * Класс для управления динамическими фонами, зависящими от времени суток
 */
object DynamicBackgroundManager {
    
    // Отображение времени суток к данным о фоне
    val backgroundMap = mapOf(
        TimeOfDay.MORNING to TimeBasedBackground(
            R.drawable.bg_water,
            Color(0xFFF8E4B7),
            Color(0xFFE1C78F),
            Color.Black.copy(alpha = 0.3f),
            Color.Black.copy(alpha = 0.05f),
            false
        ),
        TimeOfDay.DAY to TimeBasedBackground(
            R.drawable.bg_nature,
            Color(0xFF88C1FF),
            Color(0xFF5D9CEB),
            Color.Black.copy(alpha = 0.3f),
            Color.Black.copy(alpha = 0.05f),
            false
        ),
        TimeOfDay.EVENING to TimeBasedBackground(
            R.drawable.bg_air,
            Color(0xFFFF9E80),
            Color(0xFFE57373),
            Color.Black.copy(alpha = 0.4f),
            Color.Black.copy(alpha = 0.1f),
            false
        ),
        TimeOfDay.NIGHT to TimeBasedBackground(
            R.drawable.bg_space,
            Color(0xFF3F51B5),
            Color(0xFF1A237E),
            Color.Black.copy(alpha = 0.6f),
            Color.Black.copy(alpha = 0.2f),
            true
        )
    )
    
    /**
     * Получает данные о фоне для текущего времени суток
     */
    fun getCurrentBackground(): TimeBasedBackground {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val timeOfDay = TimeOfDay.fromHour(hour)
        return backgroundMap[timeOfDay] ?: backgroundMap[TimeOfDay.DAY]!!
    }
    
    /**
     * Получает приветственное сообщение для текущего времени суток
     */
    fun getWelcomeMessage(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return when (TimeOfDay.fromHour(hour)) {
            TimeOfDay.MORNING -> "Доброе утро!"
            TimeOfDay.DAY -> "Добрый день!"
            TimeOfDay.EVENING -> "Добрый вечер!"
            TimeOfDay.NIGHT -> "Доброй ночи!"
        }
    }
    
    /**
     * Рассчитывает время до следующей смены фона в миллисекундах
     */
    fun getMillisToNextChange(): Long {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        
        val nextChangeHour = when (hour) {
            in 0..4 -> 5  // Следующая смена в 5:00 (утро)
            in 5..11 -> 12 // Следующая смена в 12:00 (день)
            in 12..16 -> 17 // Следующая смена в 17:00 (вечер)
            in 17..21 -> 22 // Следующая смена в 22:00 (ночь)
            else -> 5 + 24 // Следующая смена в 5:00 следующего дня
        }
        
        val currentTimeSeconds = hour * 3600 + minute * 60 + second
        val nextChangeSeconds = (nextChangeHour % 24) * 3600
        
        val diffSeconds = if (nextChangeSeconds > currentTimeSeconds) {
            nextChangeSeconds - currentTimeSeconds
        } else {
            nextChangeSeconds + 24 * 3600 - currentTimeSeconds
        }
        
        return TimeUnit.SECONDS.toMillis(diffSeconds.toLong())
    }
}

/**
 * Composable функция для отображения динамического фона
 */
@Composable
fun DynamicBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var currentBackground by remember { mutableStateOf(DynamicBackgroundManager.getCurrentBackground()) }
    
    // Эффект для обновления фона при смене времени суток
    LaunchedEffect(key1 = Unit) {
        while (true) {
            val millisToNextChange = DynamicBackgroundManager.getMillisToNextChange()
            delay(millisToNextChange)
            currentBackground = DynamicBackgroundManager.getCurrentBackground()
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Фоновое изображение
        Image(
            painter = painterResource(id = currentBackground.backgroundResId),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Накладываем градиент поверх изображения для лучшей читаемости
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            currentBackground.overlayStart,
                            currentBackground.overlayEnd
                        )
                    )
                )
        )
        
        // Содержимое экрана
        content()
    }
} 