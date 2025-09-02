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

import androidx.compose.runtime.CompositionLocalProvider
import com.example.spybrain.presentation.theme.LocalThemePack
import android.util.Log

/**
 * @property id РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РІСЂРµРјРµРЅРё СЃСўРѕРє.
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
 * @property backgroundResId Р РµСЅСўСЃ С„РѕРЅР°.
 * @property gradientStart РќР°С‡Р°Р»СЊРЅС‹Р№ С†РІРµС‚ РіСЂР°РґРёРµРЅС‚Р°.
 * @property gradientEnd РљРѕРЅРµС‡РЅС‹Р№ С†РІРµС‚ РіСЂР°РґРёРµРЅС‚Р°.
 * @property overlayStart РќР°С‡Р°Р»СЊРЅС‹Р№ С†РІРµС‚ РЅР°Р»РѕР¶РµРЅРёСЏ.
 * @property overlayEnd РљРѕРЅРµС‡РЅС‹Р№ С†РІРµС‚ РЅР°Р»РѕР¶РµРЅРёСЏ.
 * @property isDark РџСЂРёР·РЅР°Рє С‚С‘РјРЅРѕРіРѕ С„РѕРЅР°.
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
 * @property backgroundMap РљР°СЂС‚Р° С„РѕРЅРѕРІ РїРѕ РІСЂРµРјРµРЅРё СЃСўРѕРє.
 */
object DynamicBackgroundManager {

    // РћС‚РѕР±СЂР°Р¶РµРЅРёРµ РІСЂРµРјРµРЅРё СЃСўРѕРє Рє РґР°РЅРЅС‹Рј Рѕ С„РѕРЅРµ
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
     * РџРѕР»СѓС‡Р°РµС‚ РґР°РЅРЅС‹Рµ Рѕ С„РѕРЅРµ РґР»СЏ С‚РµРєСѓС‰РµРіРѕ РІСЂРµРјРµРЅРё СЃСўРѕРє
     */
    fun getCurrentBackground(): TimeBasedBackground {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val timeOfDay = TimeOfDay.fromHour(hour)
        return backgroundMap[timeOfDay] ?: backgroundMap[TimeOfDay.DAY]!!
    }

    /**
     * РџРѕР»СѓС‡Р°РµС‚ РїСЂРёРІРµС‚СЃС‚РІРµРЅРЅРѕРµ СЃРѕРѕР±С‰РµРЅРёРµ РґР»СЏ С‚РµРєСѓС‰РµРіРѕ РІСЂРµРјРµРЅРё СЃСўРѕРє
     */
    fun getWelcomeMessage(context: Context): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return when (TimeOfDay.fromHour(hour)) {
            TimeOfDay.MORNING -> context.getString(R.string.greeting_morning)
            TimeOfDay.DAY -> context.getString(R.string.greeting_day)
            TimeOfDay.EVENING -> context.getString(R.string.greeting_evening)
            TimeOfDay.NIGHT -> context.getString(R.string.greeting_night)
        }
    }

    /**
     * Р Р°СЃСЃС‡РёС‚С‹РІР°РµС‚ РІСЂРµРјСЏ РґРѕ СЃР»РµРґСѓСЋС‰РµР№ СЃРјРµРЅС‹ С„РѕРЅР° РІ РјРёР»Р»РёСЃРµРєСўРґР°С…
     */
    fun getMillisToNextChange(): Long {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        val nextChangeHour = when (hour) {
            in 0..4 -> 5  // РЎР»РµРґСѓСЋС‰Р°СЏ СЃРјРµРЅР° РІ 5:00 (СѓС‚СЂРѕ)
            in 5..11 -> 12 // РЎР»РµРґСѓСЋС‰Р°СЏ СЃРјРµРЅР° РІ 12:00 (РґРµРЅСЊ)
            in 12..16 -> 17 // РЎР»РµРґСѓСЋС‰Р°СЏ СЃРјРµРЅР° РІ 17:00 (РІРµС‡РµСЂ)
            in 17..21 -> 22 // РЎР»РµРґСѓСЋС‰Р°СЏ СЃРјРµРЅР° РІ 22:00 (РЅРѕС‡СЊ)
            else -> 5 + 24 // РЎР»РµРґСѓСЋС‰Р°СЏ СЃРјРµРЅР° РІ 5:00 СЃР»РµРґСѓСЋС‰РµРіРѕ РґРЅСЏ
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
 * @param modifier РњРѕРґРёС„РёРєР°С‚РѕСЂ Compose.
 * @param content РљРѕРЅС‚РµРЅС‚ Compose.
 */
@Composable
fun DynamicBackground(
    modifier: Modifier = Modifier,
    lottieKeyOverride: String? = null,
    greetingOverride: String? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val timeOfDay = remember { getCurrentTimeOfDay() }
    val backgroundData = remember(timeOfDay) { getBackgroundForTimeOfDay(timeOfDay) }
    val greeting = remember(timeOfDay, greetingOverride) { greetingOverride ?: getGreetingForTimeOfDay(context, timeOfDay) }

    // РђРЅРёРјР°С†РёСЏ РґР»СЏ РїР»Р°РІРЅРѕРіРѕ РїРµСЂРµС…РѕРґР°
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
    ) {
        // Получаем тему для анимации
        val themePack = LocalThemePack.current
        
        // Проверяем настройки фонов реактивно
        val context = LocalContext.current
        var useVideoBackgrounds by remember { mutableStateOf(false) }
        var useAnimatedBackgrounds by remember { mutableStateOf(true) }
        
        Log.d("DynamicBackground", "Initial state - Video: $useVideoBackgrounds, Animated: $useAnimatedBackgrounds")
        
        // Используем collectAsState вместо LaunchedEffect для избежания LeftCompositionCancellationException
        val videoBackgroundsEnabled by remember {
            try {
                val ep = dagger.hilt.android.EntryPointAccessors.fromApplication(
                    context.applicationContext, 
                    com.example.spybrain.di.AppEntryPoints::class.java
                )
                ep.settingsDataStore().videoBackgroundsEnabledFlow
            } catch (e: Exception) {
                Log.e("DynamicBackground", "Error accessing video background settings", e)
                kotlinx.coroutines.flow.flowOf(false)
            }
        }.collectAsState(initial = false)
        
        val animatedBackgroundsEnabled by remember {
            try {
                val ep = dagger.hilt.android.EntryPointAccessors.fromApplication(
                    context.applicationContext, 
                    com.example.spybrain.di.AppEntryPoints::class.java
                )
                ep.settingsDataStore().animatedBackgroundsEnabledFlow
            } catch (e: Exception) {
                Log.e("DynamicBackground", "Error accessing animated background settings", e)
                kotlinx.coroutines.flow.flowOf(true)
            }
        }.collectAsState(initial = true)
        
        // Обновляем локальные состояния
        useVideoBackgrounds = videoBackgroundsEnabled
        useAnimatedBackgrounds = animatedBackgroundsEnabled
        
        // Выбираем тип фона с учетом темы
        val currentThemeKey = when (themePack.name.lowercase()) {
            "water" -> "water"
            "space", "cosmos" -> "space"
            "nature" -> "nature"
            "air" -> "air"
            else -> "water" // дефолт
        }
        
        Log.d("DynamicBackground", "Theme: ${themePack.name}, Key: $currentThemeKey, Video: $useVideoBackgrounds, Animated: $useAnimatedBackgrounds")
        
                when {
            useVideoBackgrounds -> {
                Log.d("DynamicBackground", "Rendering VideoOrCanvasBackground - Video enabled")
                VideoOrCanvasBackground(
                    themeKey = currentThemeKey,
                    useVideo = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
            useAnimatedBackgrounds -> {
                Log.d("DynamicBackground", "Rendering AnimatedBackground - Animated enabled")
                AnimatedBackground(
                    themeKey = currentThemeKey,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                // Статичный фон (градиент) - только если оба типа отключены
                Log.d("DynamicBackground", "Rendering static gradient background - both disabled")
                Box(
                modifier = Modifier
                    .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    themePack.colorScheme.primary.copy(alpha = 0.3f),
                                    themePack.colorScheme.secondary.copy(alpha = 0.1f)
                                )
                            )
                        )
                )
            }
        }

        // Приветствие убрано - теперь отображается только в MainScreen

        // РћСЃРЅРѕРІРЅРѕР№ РєРѕРЅС‚РµРЅС‚
        content()
    }
}

/**
 * РџРѕР»СѓС‡Р°РµС‚ С‚РµРєСѓС‰РµРµ РІСЂРµРјСЏ СЃСўРѕРє
 */
private fun getCurrentTimeOfDay(): TimeOfDay {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    return TimeOfDay.fromHour(hour)
}

/**
 * РџРѕР»СѓС‡Р°РµС‚ РґР°РЅРЅС‹Рµ Рѕ С„РѕРЅРµ РґР»СЏ РІСЂРµРјРµРЅРё СЃСўРѕРє
 */
private fun getBackgroundForTimeOfDay(timeOfDay: TimeOfDay): TimeBasedBackground {
    return DynamicBackgroundManager.backgroundMap[timeOfDay]
        ?: DynamicBackgroundManager.backgroundMap[TimeOfDay.DAY]!!
}

/**
 * РџРѕР»СѓС‡Р°РµС‚ РіСЂР°РґРёРµРЅС‚РЅС‹Рµ С†РІРµС‚Р° РґР»СЏ РІСЂРµРјРµРЅРё СЃСўРѕРє
 */
private fun getGradientColorsForTimeOfDay(timeOfDay: TimeOfDay): List<Color> {
    val background = getBackgroundForTimeOfDay(timeOfDay)
    return listOf(background.gradientStart, background.gradientEnd)
}

/**
 * РџРѕР»СѓС‡Р°РµС‚ РїСЂРёРІРµС‚СЃС‚РІРёРµ РґР»СЏ РІСЂРµРјРµРЅРё СЃСўРѕРє
 */
private fun getGreetingForTimeOfDay(context: Context, timeOfDay: TimeOfDay): String {
    return when (timeOfDay) {
        TimeOfDay.MORNING -> context.getString(R.string.greeting_morning)
        TimeOfDay.DAY -> context.getString(R.string.greeting_day)
        TimeOfDay.EVENING -> context.getString(R.string.greeting_evening)
        TimeOfDay.NIGHT -> context.getString(R.string.greeting_night)
    }
}
