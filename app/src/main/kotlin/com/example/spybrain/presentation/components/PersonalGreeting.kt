package com.example.spybrain.presentation.components

import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spybrain.R
import java.text.SimpleDateFormat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CardDefaults
import java.util.Calendar

/**
 * РџРµСЂСЃРѕРЅР°Р»СЊРЅРѕРµ РїСЂРёРІРµС‚СЃС‚РІРёРµ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 * @param userName РРјСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 * @param modifier РњРѕРґРёС„РёРєР°С‚РѕСЂ Compose.
 */
@Composable
fun personalGreeting(
    userName: String = "",
    modifier: Modifier = Modifier
) {
    var currentSlogan by remember { mutableStateOf("") }

    val context = LocalContext.current
    // Р“РµРЅРµСЂРёСЂСўРЅРі РїСЂРё РїРµСЂРІРѕРј СЂРµРЅРґРµСЂРµ
    LaunchedEffect(Unit) {
        currentSlogan = motivationalQuote(context)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // РџСЂРёРІРµС‚СЃС‚РІРёРµ РїРѕ РІСЂРµРјРµРЅРё СЃСўРѕРє
        val greeting = getTimeBasedGreeting(LocalContext.current)
        val displayName = if (userName.isNotEmpty()) userName else stringResource(R.string.default_user_name)

        Text(
            text = "$greeting, $displayName!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // AI-Р»РѕР·СўРЅРі
        AnimatedVisibility(
            visible = currentSlogan.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = currentSlogan,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

/**
 * РџРѕР»СўС‡РёС‚СЊ РїСЂРёРІРµС‚СЃС‚РІРёРµ РїРѕ РІСЂРµРјРµРЅРё СЃСўРѕРє.
 * @param context РљРѕРЅС‚РµРєСЃС‚.
 * @return РџСЂРёРІРµС‚СЃС‚РІРёРµ.
 */
private fun getTimeBasedGreeting(context: Context): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    return when {
        hour < 6 -> context.getString(R.string.greeting_night)
        hour < 12 -> context.getString(R.string.greeting_morning)
        hour < 17 -> context.getString(R.string.greeting_day)
        hour < 23 -> context.getString(R.string.greeting_evening)
        else -> context.getString(R.string.greeting_night)
    }
}

/**
 * РЎРіРµРЅРµСЂРёСЂРѕРІР°С‚СЊ РјРѕС‚РёРІР°С†РёРѕРЅРЅС‹Р№ СЃР»РѕРіР°РЅ.
 * @param context РљРѕРЅС‚РµРєСЃС‚.
 * @return РЎР»СўС‡Р°Р№РЅС‹Р№ СЃР»РѕРіР°РЅ.
 */
private fun motivationalQuote(context: Context): String {
    val slogans = listOf(
        context.getString(R.string.motivation_1),
        context.getString(R.string.motivation_2),
        context.getString(R.string.motivation_3),
        context.getString(R.string.motivation_4),
        context.getString(R.string.motivation_5),
        context.getString(R.string.motivation_6),
        context.getString(R.string.motivation_7),
        context.getString(R.string.motivation_8),
        context.getString(R.string.motivation_9),
        context.getString(R.string.motivation_10),
        context.getString(R.string.motivation_11),
        context.getString(R.string.motivation_12),
        context.getString(R.string.motivation_13),
        context.getString(R.string.motivation_14),
        context.getString(R.string.motivation_15),
        context.getString(R.string.motivation_16),
        context.getString(R.string.motivation_17),
        context.getString(R.string.motivation_18),
        context.getString(R.string.motivation_19),
        context.getString(R.string.motivation_20)
    )

    return slogans.random()
}

/**
 * РџСЂРёРІРµС‚СЃС‚РІРёРµ РїРѕ РІСЂРµРјРµРЅРё СЃСўРѕРє.
 */
@Composable
fun timeBasedGreeting() {
    val currentTime = remember { System.currentTimeMillis() }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Text(
        text = timeFormat.format(Date(currentTime)),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

fun personalGreetingDetails(/* параметры */) {
    // ... existing code ...
}
