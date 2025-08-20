package com.example.spybrain.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Star
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spybrain.presentation.navigation.BottomNavigationBar
import com.example.spybrain.presentation.navigation.NavGraph
import com.example.spybrain.presentation.theme.DynamicBackground
import androidx.compose.runtime.CompositionLocalProvider
import com.example.spybrain.presentation.theme.LocalIconPack
import com.example.spybrain.presentation.theme.LocalThemePack
import com.example.spybrain.presentation.theme.ThemePacks
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import com.example.spybrain.service.AmbientMusicService
import com.example.spybrain.presentation.settings.SettingsViewModel
import com.example.spybrain.presentation.navigation.Screen
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme

/**
 * Р“Р»Р°РІРЅС‹Р№ СЌРєСЂР°РЅ РїСЂРёР»РѕР¶РµРЅРёСЏ СЃ РЅР°РІРёРіР°С†РёРµР№.
 * @param navController РљРѕРЅС‚СЂРѕР»Р»РµСЂ РЅР°РІРёРіР°С†РёРё.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    // Автозапуск фоновой музыки при входе
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settings by settingsViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(settings.ambientEnabled, settings.ambientTrack) {
        // Больше не автозапускаем музыку. Только гарантируем стоп, если выключено.
        if (!settings.ambientEnabled || settings.ambientTrack.isEmpty()) {
            runCatching {
                val intent = Intent(context, AmbientMusicService::class.java).apply { action = AmbientMusicService.ACTION_STOP }
                context.startService(intent)
            }
        }
    }

    val themePack = ThemePacks.themePackFor(settings.theme)
    val name = settings.userName.ifBlank { "друг" }
    val greetingPrefix = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Доброго утра"
        in 12..16 -> "Доброго дня"
        in 17..21 -> "Доброго вечера"
        else -> "Доброй ночи"
    }
    val greeting = "$greetingPrefix, ${name.replaceFirstChar { it.uppercase() }}!"
    CompositionLocalProvider(LocalThemePack provides themePack, LocalIconPack provides themePack.icons) {
    DynamicBackground(greetingOverride = greeting) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { /* нижняя панель в MainActivity */ }
        ) { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                // Приветствие по времени суток (без имени пока)
                val timeGreeting = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
                    in 5..11 -> "Доброе утро"
                    in 12..16 -> "Добрый день"
                    in 17..21 -> "Добрый вечер"
                    else -> "Доброй ночи"
                }
                // Приветствие и мотивация
                val quotes = listOf(
                    "Познай себя — через медитацию.",
                    "Расслабление — искусство, которому можно научиться.",
                    "Дыши глубже, думай яснее.",
                    "Спокойствие — твоя суперсила.",
                    "Твое внимание — твоя энергия."
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = timeGreeting,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = quotes.random(),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }

                // Узлы вкладок
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.size(24.dp))
                    HomeNode(
                        icon = Icons.Filled.Star,
                        label = context.getString(com.example.spybrain.R.string.meditations),
                        onClick = { navController.navigate(Screen.Meditation.route) },
                        sizeDp = 120,
                        offsetYDp = 12
                    )
                    Spacer(Modifier.weight(1f))
                    HomeNode(
                        icon = Icons.Filled.Headphones,
                        label = context.getString(com.example.spybrain.R.string.meditation_guided_tab_title),
                        onClick = { navController.navigate(Screen.Meditation.route) },
                        sizeDp = 136,
                        offsetYDp = -8
                    )
                    Spacer(Modifier.weight(1f))
                    HomeNode(
                        icon = Icons.Filled.Air,
                        label = context.getString(com.example.spybrain.R.string.breathing_title),
                        onClick = { navController.navigate(Screen.Breathing.route) },
                        sizeDp = 120,
                        offsetYDp = 16
                    )
                    Spacer(Modifier.size(24.dp))
                }

                // Анимированные «синапсы» между узлами
                SynapsesOverlay()
            }
        }
    }
    }
}

@Composable
private fun HomeNode(icon: ImageVector, label: String, onClick: () -> Unit, sizeDp: Int = 100, offsetYDp: Int = 0) {
    Card(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .offset(y = offsetYDp.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size((sizeDp * 0.4f).dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun SynapsesOverlay() {
    val infinite = rememberInfiniteTransition(label = "synapses")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2000), repeatMode = RepeatMode.Restart),
        label = "phase"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val y = size.height * 0.48f
        val x1 = size.width * 0.22f
        val x2 = size.width * 0.5f
        val x3 = size.width * 0.78f

        val path1 = Path().apply {
            moveTo(x1, y)
            cubicTo(x1 + 120f, y - 80f, x2 - 120f, y + 80f, x2, y)
        }
        val path2 = Path().apply {
            moveTo(x2, y)
            cubicTo(x2 + 120f, y - 80f, x3 - 120f, y + 80f, x3, y)
        }

        drawPath(path1, color = Color.White.copy(alpha = 0.22f), style = Stroke(width = 4f, cap = StrokeCap.Round))
        drawPath(path2, color = Color.White.copy(alpha = 0.22f), style = Stroke(width = 4f, cap = StrokeCap.Round))

        // «Импульсы» — движущиеся точки по кривым
        fun drawPulse(xStart: Float, xEnd: Float) {
            val t = phase
            val x = xStart + (xEnd - xStart) * t
            val yDot = y + kotlin.math.sin(t * 3.14159f) * 14f
            drawCircle(Color.Cyan.copy(alpha = 0.9f), radius = 7f, center = androidx.compose.ui.geometry.Offset(x, yDot))
        }
        drawPulse(x1, x2)
        drawPulse(x2, x3)
    }
}

