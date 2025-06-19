package com.example.spybrain.presentation.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spybrain.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PersonalGreeting(
    userName: String = "",
    modifier: Modifier = Modifier
) {
    var currentSlogan by remember { mutableStateOf("") }
    
    // Генерируем лозунг при первом рендере
    LaunchedEffect(Unit) {
        currentSlogan = generateMotivationalSlogan(LocalContext.current)
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Приветствие по времени суток
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
        
        // AI-лозунг
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

private fun generateMotivationalSlogan(context: Context): String {
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

@Composable
fun TimeBasedGreeting() {
    val currentTime = remember { System.currentTimeMillis() }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    
    Text(
        text = timeFormat.format(Date(currentTime)),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
} 