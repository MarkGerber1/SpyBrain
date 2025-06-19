package com.example.spybrain.presentation.breathing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import com.example.spybrain.domain.model.BreathingPattern
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import com.example.spybrain.presentation.breathing.BreathingContract.BreathingPhase
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.spybrain.util.VibrationUtil
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(
    navController: NavHostController,
    viewModel: BreathingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var activePattern by remember { mutableStateOf<BreathingPattern?>(null) }
    var selectedCategory by remember { mutableStateOf("all") }
    
    // Эффект для обработки уведомлений и ошибок
    LaunchedEffect(key1 = viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BreathingContract.Effect.ShowError -> {
                    VibrationUtil.longVibration(context)
                    Toast.makeText(context, effect.error.toString(), Toast.LENGTH_SHORT).show()
                }
                is BreathingContract.Effect.Vibrate -> {
                    // Вибрация при смене фазы дыхания
                    VibrationUtil.breathingVibration(context)
                }
                is BreathingContract.Effect.Speak -> {
                    // Голосовая подсказка
                    Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Дыхательные практики") },
                actions = {
                    // Кнопка для создания собственного шаблона
                    IconButton(onClick = { 
                        VibrationUtil.shortVibration(context)
                        navController.navigate("pattern_builder")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Создать шаблон")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.currentPattern != null && state.currentPhase != BreathingContract.BreathingPhase.Idle) {
            // Активная сессия дыхания
            ActiveBreathingSession(
                state = state,
                onStop = { viewModel.setEvent(BreathingContract.Event.StopPattern) }
            )
        } else {
            // Список шаблонов с категориями
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                if (state.patterns.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет доступных шаблонов дыхания",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    // Категории
                    CategoryTabs(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Фильтрованные паттерны
                    val filteredPatterns = when (selectedCategory) {
                        "relaxation" -> state.patterns.filter { 
                            it.id in listOf("classic_meditation", "breathing_478", "sleep_breathing") 
                        }
                        "energy" -> state.patterns.filter { 
                            it.id in listOf("fire_breath", "energy_breathing", "lion_breath") 
                        }
                        "focus" -> state.patterns.filter { 
                            it.id in listOf("box_breathing", "focus_breathing", "creative_breathing") 
                        }
                        else -> state.patterns
                    }
                    
                    PatternsList(
                        patterns = filteredPatterns,
                        onPatternSelected = { pattern ->
                            VibrationUtil.achievementVibration(context)
                            activePattern = pattern
                            viewModel.setEvent(BreathingContract.Event.StartPattern(pattern))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val context = LocalContext.current
    val categories = listOf(
        "all" to "Все",
        "relaxation" to "Расслабление",
        "energy" to "Энергия",
        "focus" to "Концентрация"
    )
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { (id, name) ->
            FilterChip(
                onClick = { 
                    VibrationUtil.shortVibration(context)
                    onCategorySelected(id) 
                },
                label = { Text(name) },
                selected = selectedCategory == id,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun PatternsList(
    patterns: List<BreathingPattern>,
    onPatternSelected: (BreathingPattern) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(patterns) { pattern ->
            PatternCard(pattern = pattern, onClick = { onPatternSelected(pattern) })
        }
    }
}

@Composable
fun PatternCard(
    pattern: BreathingPattern,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = pattern.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = pattern.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Визуализация схемы дыхания
            PatternPreview(pattern = pattern)
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Вдох: ${pattern.inhaleSeconds}с • Задержка: ${pattern.holdAfterInhaleSeconds}с • " +
                      "Выдох: ${pattern.exhaleSeconds}с • Задержка: ${pattern.holdAfterExhaleSeconds}с",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Циклов: ${pattern.totalCycles}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PatternPreview(pattern: BreathingPattern) {
    val totalDuration = pattern.inhaleSeconds + pattern.holdAfterInhaleSeconds + 
                         pattern.exhaleSeconds + pattern.holdAfterExhaleSeconds
    
    // Проверяем, что общая длительность больше 0, иначе используем fallback
    val safeTotalDuration = if (totalDuration > 0) totalDuration else 1
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(
                    if (pattern.inhaleSeconds > 0) pattern.inhaleSeconds.toFloat() / safeTotalDuration else 0.25f
                )
                .fillMaxHeight()
                .background(Color(0xFF81D4FA))
        )
        Box(
            modifier = Modifier
                .weight(
                    if (pattern.holdAfterInhaleSeconds > 0) pattern.holdAfterInhaleSeconds.toFloat() / safeTotalDuration else 0.25f
                )
                .fillMaxHeight()
                .background(Color(0xFF64B5F6))
        )
        Box(
            modifier = Modifier
                .weight(
                    if (pattern.exhaleSeconds > 0) pattern.exhaleSeconds.toFloat() / safeTotalDuration else 0.25f
                )
                .fillMaxHeight()
                .background(Color(0xFF42A5F5))
        )
        Box(
            modifier = Modifier
                .weight(
                    if (pattern.holdAfterExhaleSeconds > 0) pattern.holdAfterExhaleSeconds.toFloat() / safeTotalDuration else 0.25f
                )
                .fillMaxHeight()
                .background(Color(0xFF2196F3))
        )
    }
}

@Composable
fun ActiveBreathingSession(
    state: BreathingContract.State,
    onStop: () -> Unit
) {
    val pattern = state.currentPattern ?: return
    val progress = state.cycleProgress
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Верхняя часть с информацией о текущем шаблоне
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = pattern.name,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when(state.currentPhase) {
                    BreathingContract.BreathingPhase.Inhale -> stringResource(R.string.breath_in)
                    BreathingContract.BreathingPhase.HoldAfterInhale -> stringResource(R.string.breath_hold)
                    BreathingContract.BreathingPhase.Exhale -> stringResource(R.string.breath_out)
                    BreathingContract.BreathingPhase.HoldAfterExhale -> stringResource(R.string.breath_hold)
                    BreathingContract.BreathingPhase.Idle -> ""
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Цикл ${pattern.totalCycles - state.remainingCycles + 1} из ${pattern.totalCycles}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Анимация дыхания
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedBreathCircle(
                phase = state.currentPhase,
                progress = progress
            )
        }
        
        // Кнопки управления
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onStop,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Stop, contentDescription = "Остановить")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Остановить")
            }
        }
    }
}

@Composable
fun AnimatedBreathCircle(
    phase: BreathingContract.BreathingPhase,
    progress: Float
) {
    val sizeMultiplier by animateFloatAsState(
        targetValue = when (phase) {
            BreathingContract.BreathingPhase.Inhale -> 0.3f + progress * 0.7f
            BreathingContract.BreathingPhase.HoldAfterInhale -> 1.0f
            BreathingContract.BreathingPhase.Exhale -> 1.0f - progress * 0.7f
            BreathingContract.BreathingPhase.HoldAfterExhale -> 0.3f
            BreathingContract.BreathingPhase.Idle -> 0.5f
        },
        animationSpec = tween(300),
        label = "size"
    )
    
    val color = when (phase) {
        BreathingContract.BreathingPhase.Inhale -> Color(0xFF81D4FA)
        BreathingContract.BreathingPhase.HoldAfterInhale -> Color(0xFF64B5F6)
        BreathingContract.BreathingPhase.Exhale -> Color(0xFF42A5F5)
        BreathingContract.BreathingPhase.HoldAfterExhale -> Color(0xFF2196F3)
        BreathingContract.BreathingPhase.Idle -> Color(0xFF1976D2)
    }
    
    Box(
        modifier = Modifier.size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        // Фоновый круг
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxSize = minOf(size.width, size.height)
            val radius = (maxSize / 2 * 0.9).toFloat()
            
            // Внешний круг (контур)
            drawCircle(
                color = color.copy(alpha = 0.3f),
                radius = radius,
                center = Offset(x = centerX, y = centerY),
                style = Stroke(width = 8.dp.toPx())
            )
            
            // Прогресс
            val left = centerX - radius
            val top = centerY - radius
            val arcSize = radius * 2
            
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(x = left, y = top),
                size = Size(width = arcSize, height = arcSize),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        // Внутренний анимированный круг
        Box(
            modifier = Modifier
                .fillMaxSize(sizeMultiplier)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when(phase) {
                    BreathingContract.BreathingPhase.Inhale -> stringResource(R.string.breath_in)
                    BreathingContract.BreathingPhase.HoldAfterInhale -> stringResource(R.string.breath_hold)
                    BreathingContract.BreathingPhase.Exhale -> stringResource(R.string.breath_out)
                    BreathingContract.BreathingPhase.HoldAfterExhale -> stringResource(R.string.breath_hold)
                    BreathingContract.BreathingPhase.Idle -> stringResource(R.string.breath_ready)
                },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
} 