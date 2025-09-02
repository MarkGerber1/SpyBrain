package com.example.spybrain.presentation

import com.example.spybrain.R
import com.example.spybrain.utils.QuoteManager
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
import androidx.compose.foundation.background
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
    // Убираем автозапуск фоновой музыки
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settings by settingsViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Фоновая музыка теперь запускается только по явному действию пользователя

    val themePack = ThemePacks.themePackFor(settings.theme)
    val name = settings.userName.ifBlank { context.getString(R.string.default_user_friend) }
    val greetingPrefix = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
        in 5..11 -> context.getString(R.string.greeting_good_morning)
        in 12..16 -> context.getString(R.string.greeting_good_afternoon)
        in 17..21 -> context.getString(R.string.greeting_good_evening)
        else -> context.getString(R.string.greeting_good_night)
    }
    val greeting = "$greetingPrefix, ${name.replaceFirstChar { it.uppercase() }}!"
    CompositionLocalProvider(LocalThemePack provides themePack, LocalIconPack provides themePack.icons) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { /* нижняя панель в MainActivity */ }
        ) { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                // Приветствие и мотивация - получаем случайную цитату при каждом входе
                val currentQuote = QuoteManager.getRandomQuote(context)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = currentQuote,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Узлы вкладок в виде неправильного треугольника
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                ) {
                    // Верхний левый узел
                    HomeNode(
                        icon = Icons.Filled.Star,
                        label = context.getString(com.example.spybrain.R.string.meditations),
                        onClick = { navController.navigate(Screen.Meditation.route) },
                        sizeDp = 140,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = 40.dp, y = 60.dp)
                    )
                    
                    // Верхний правый узел
                    HomeNode(
                        icon = Icons.Filled.Headphones,
                        label = context.getString(com.example.spybrain.R.string.meditation_guided_tab_title),
                        onClick = { navController.navigate(Screen.Meditation.route) },
                        sizeDp = 160,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-40).dp, y = 80.dp)
                    )
                    
                    // Нижний центральный узел
                    HomeNode(
                        icon = Icons.Filled.Air,
                        label = context.getString(com.example.spybrain.R.string.breathing_title),
                        onClick = { navController.navigate(Screen.Breathing.route) },
                        sizeDp = 150,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-100).dp)
                    )
                }

                // Анимированные «синапсы» между узлами
                SynapsesOverlay()
            }
        }
    }
}

@Composable
private fun HomeNode(
    icon: ImageVector, 
    label: String, 
    onClick: () -> Unit, 
    sizeDp: Int = 100, 
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.Black,
                    modifier = Modifier.size((sizeDp * 0.35f).dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = label, 
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), 
                    color = Color.Black, 
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SynapsesOverlay() {
    val infinite = rememberInfiniteTransition(label = "synapses")
    val phase1 by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(3000), repeatMode = RepeatMode.Restart),
        label = "phase1"
    )
    val phase2 by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2500, delayMillis = 500), repeatMode = RepeatMode.Restart),
        label = "phase2"
    )
    val phase3 by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2800, delayMillis = 1000), repeatMode = RepeatMode.Restart),
        label = "phase3"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Позиции узлов в треугольнике
        val topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.25f, size.height * 0.35f)
        val topRight = androidx.compose.ui.geometry.Offset(size.width * 0.75f, size.height * 0.35f)
        val bottomCenter = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.65f)

        // Пути между узлами с разными траекториями
        val path1 = Path().apply {
            moveTo(topLeft.x, topLeft.y)
            cubicTo(
                topLeft.x + 100f, topLeft.y - 50f,
                topRight.x - 100f, topRight.y - 50f,
                topRight.x, topRight.y
            )
        }
        
        val path2 = Path().apply {
            moveTo(topRight.x, topRight.y)
            cubicTo(
                topRight.x - 80f, topRight.y + 80f,
                bottomCenter.x + 80f, bottomCenter.y - 80f,
                bottomCenter.x, bottomCenter.y
            )
        }
        
        val path3 = Path().apply {
            moveTo(bottomCenter.x, bottomCenter.y)
            cubicTo(
                bottomCenter.x - 80f, bottomCenter.y - 80f,
                topLeft.x + 80f, topLeft.y + 80f,
                topLeft.x, topLeft.y
            )
        }

        // Рисуем пути
        drawPath(path1, color = Color.White.copy(alpha = 0.6f), style = Stroke(width = 4f, cap = StrokeCap.Round))
        drawPath(path2, color = Color.White.copy(alpha = 0.6f), style = Stroke(width = 4f, cap = StrokeCap.Round))
        drawPath(path3, color = Color.White.copy(alpha = 0.6f), style = Stroke(width = 4f, cap = StrokeCap.Round))

        // Импульсы по разным траекториям
        fun drawPulse(path: Path, phase: Float, color: Color) {
            val t = phase
            // Простая интерполяция для демонстрации
            val pulseSize = 12f + kotlin.math.sin(phase * 6.28f) * 5f
            
            // Рисуем импульс в разных точках пути
            val points = listOf(
                androidx.compose.ui.geometry.Offset(topLeft.x + (topRight.x - topLeft.x) * t, topLeft.y + (topRight.y - topLeft.y) * t),
                androidx.compose.ui.geometry.Offset(topRight.x + (bottomCenter.x - topRight.x) * t, topRight.y + (bottomCenter.y - topRight.y) * t),
                androidx.compose.ui.geometry.Offset(bottomCenter.x + (topLeft.x - bottomCenter.x) * t, bottomCenter.y + (topLeft.y - bottomCenter.y) * t)
            )
            
            val point = when {
                path == path1 -> points[0]
                path == path2 -> points[1]
                else -> points[2]
            }
            
            drawCircle(color, radius = pulseSize, center = point)
        }
        
        drawPulse(path1, phase1, Color.Cyan.copy(alpha = 1.0f))
        drawPulse(path2, phase2, Color.Magenta.copy(alpha = 1.0f))
        drawPulse(path3, phase3, Color.Yellow.copy(alpha = 1.0f))
    }
}

