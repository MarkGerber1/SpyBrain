// FIXME UI/UX финал 09.05.2025
// FIXME билд-фикс 09.05.2025: заменены ресурсы фонов на существующие
package com.example.spybrain.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.spybrain.R
import java.util.Calendar

@Composable
fun SmartWelcomeScreen(
    isOffline: Boolean = false,
    onQuickAction: (String) -> Unit = {}
) {
    // Определяем время суток
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val (greeting, bgRes) = when (hour) {
        in 5..11 -> "Доброе утро!" to com.example.spybrain.R.drawable.bg_water
        in 12..17 -> "Добрый день!" to com.example.spybrain.R.drawable.bg_nature
        in 18..22 -> "Добрый вечер!" to com.example.spybrain.R.drawable.bg_space
        else -> "Доброй ночи!" to com.example.spybrain.R.drawable.bg_air
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(greeting, style = MaterialTheme.typography.headlineLarge, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            if (isOffline) {
                Text("Нет соединения с сетью", color = Color.Red)
            } else {
                // Быстрые действия
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { onQuickAction("breathing") }) { Text("Быстрое дыхание") }
                    Button(onClick = { onQuickAction("meditation") }) { Text("Быстрая медитация") }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            // Анимированный круговой индикатор (например, TTS)
            var progress by remember { mutableStateOf(0.7f) } // TODO: заменить на реальный прогресс
            CircularProgressIndicator(progress = progress, modifier = Modifier.size(120.dp), color = Color.White)
        }
    }
} 