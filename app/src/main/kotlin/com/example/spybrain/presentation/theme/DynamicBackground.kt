package com.example.spybrain.presentation.theme

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val timeOfDay = remember { getCurrentTimeOfDay() }
    val backgroundData = remember(timeOfDay) { getBackgroundForTimeOfDay(timeOfDay) }
    val greeting = remember(timeOfDay) { getGreetingForTimeOfDay(context, timeOfDay) }
    
    // Анимация для плавного перехода
    val infiniteTransition = rememberInfiniteTransition()
    val backgroundAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundData.gradientStart.copy(alpha = backgroundAlpha),
                        backgroundData.gradientEnd.copy(alpha = backgroundAlpha)
                    )
                )
            )
    ) {
        // Фоновое изображение
        Image(
            painter = painterResource(id = backgroundData.backgroundResId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f),
            contentScale = ContentScale.Crop
        )
        
        // Приветствие с анимацией
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(),
            modifier = Modifier.animateContentSize()
        ) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp)
            )
        }
        
        // Основной контент
        content()
    }
}

/**
 * Получает текущее время суток
 */
private fun getCurrentTimeOfDay(): TimeOfDay {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    return TimeOfDay.fromHour(hour)
}

/**
 * Получает данные о фоне для времени суток
 */
private fun getBackgroundForTimeOfDay(timeOfDay: TimeOfDay): TimeBasedBackground {
    return DynamicBackgroundManager.backgroundMap[timeOfDay] 
        ?: DynamicBackgroundManager.backgroundMap[TimeOfDay.DAY]!!
}

/**
 * Получает градиентные цвета для времени суток
 */
private fun getGradientColorsForTimeOfDay(timeOfDay: TimeOfDay): List<Color> {
    val background = getBackgroundForTimeOfDay(timeOfDay)
    return listOf(background.gradientStart, background.gradientEnd)
}

/**
 * Получает приветствие для времени суток
 */
private fun getGreetingForTimeOfDay(context: Context, timeOfDay: TimeOfDay): String {
    return when (timeOfDay) {
        TimeOfDay.MORNING -> "Доброе утро! ☀️"
        TimeOfDay.DAY -> "Добрый день! 🌤️"
        TimeOfDay.EVENING -> "Добрый вечер! 🌅"
        TimeOfDay.NIGHT -> "Доброй ночи! 🌙"
    }
} 