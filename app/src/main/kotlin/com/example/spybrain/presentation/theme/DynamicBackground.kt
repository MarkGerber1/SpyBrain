package com.example.spybrain.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloat
import com.example.spybrain.data.datastore.SettingsDataStore

/**
 * @property id Идентификатор времени суток.
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
 * @property backgroundResId Ресурс фона.
 * @property gradientStart Начальный цвет градиента.
 * @property gradientEnd Конечный цвет градиента.
 * @property overlayStart Начальный цвет наложения.
 * @property overlayEnd Конечный цвет наложения.
 * @property isDark Признак тёмного фона.
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
 * @property backgroundMap Карта фонов по времени суток.
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

    fun getCurrentBackground(): TimeBasedBackground {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val timeOfDay = TimeOfDay.fromHour(hour)
        return backgroundMap[timeOfDay] ?: backgroundMap[TimeOfDay.DAY]!!
    }

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

    fun getMillisToNextChange(): Long {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        val nextChangeHour = when (hour) {
            in 0..4 -> 5
            in 5..11 -> 12
            in 12..16 -> 17
            in 17..21 -> 22
            else -> 5 + 24
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

@Composable
fun DynamicBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val timeOfDay = remember { getCurrentTimeOfDay() }
    val backgroundData = remember(timeOfDay) { getBackgroundForTimeOfDay(timeOfDay) }
    val greeting = remember(timeOfDay) { getGreetingForTimeOfDay(context, timeOfDay) }

    // Respect selected theme for background image
    val settings = remember { SettingsDataStore(context) }
    val theme by settings.themeFlow.collectAsState(initial = "nature")
    val themedBackgroundRes = when (theme) {
        "water" -> R.drawable.bg_water
        "space" -> R.drawable.bg_space
        "air" -> R.drawable.bg_air
        else -> R.drawable.bg_nature
    }

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
        // Background image overridden by theme
        Image(
            painter = painterResource(id = themedBackgroundRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f),
            contentScale = ContentScale.Crop
        )

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

        content()
    }
}

private fun getCurrentTimeOfDay(): TimeOfDay {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    return TimeOfDay.fromHour(hour)
}

private fun getBackgroundForTimeOfDay(timeOfDay: TimeOfDay): TimeBasedBackground {
    return DynamicBackgroundManager.backgroundMap[timeOfDay]
        ?: DynamicBackgroundManager.backgroundMap[TimeOfDay.DAY]!!
}

private fun getGradientColorsForTimeOfDay(timeOfDay: TimeOfDay): List<Color> {
    val background = getBackgroundForTimeOfDay(timeOfDay)
    return listOf(background.gradientStart, background.gradientEnd)
}

private fun getGreetingForTimeOfDay(context: Context, timeOfDay: TimeOfDay): String {
    return when (timeOfDay) {
        TimeOfDay.MORNING -> "Доброе утро! ☀️"
        TimeOfDay.DAY -> "Добрый день! 🌤️"
        TimeOfDay.EVENING -> "Добрый вечер! 🌇"
        TimeOfDay.NIGHT -> "Доброй ночи! 🌙"
    }
}
