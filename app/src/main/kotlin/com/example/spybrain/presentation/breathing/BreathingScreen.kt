package com.example.spybrain.presentation.breathing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spybrain.R
import com.example.spybrain.domain.model.BreathingPattern
import com.example.spybrain.presentation.theme.DynamicBackground
import com.example.spybrain.util.VibrationUtil
import android.widget.Toast
import androidx.activity.compose.BackHandler
import com.example.spybrain.presentation.components.InfoBottomSheet

/**
 * Экран дыхательных практик.
 * @param navController Контроллер навигации.
 * @param viewModel ViewModel для управления состоянием экрана.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun breathingScreen(
    navController: NavHostController,
    viewModel: BreathingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var activePattern by remember { mutableStateOf<BreathingPattern?>(null) }
    var selectedCategory by remember { mutableStateOf("all") }
    var showInfoSheet by remember { mutableStateOf(false) }
    var confirmStopDialog by remember { mutableStateOf(false) }

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

    DynamicBackground {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.breathing_title)) },
                actions = {
                    // Кнопка для создания собственного шаблона
                    IconButton(onClick = {
                        VibrationUtil.shortVibration(context)
                        navController.navigate("pattern_builder")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.breathing_create_pattern))
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
                onStop = { viewModel.setEvent(BreathingContract.Event.StopPattern) },
                onInfo = { showInfoSheet = true }
            )

            // BottomSheet с информацией
            if (showInfoSheet) {
                InfoBottomSheet(
                    title = stringResource(id = R.string.breathing_title),
                    tabs = listOf(
                        stringResource(id = R.string.breathing_why_title) to stringResource(id = R.string.breathing_why_text),
                        stringResource(id = R.string.breathing_benefits_title) to stringResource(id = R.string.breathing_benefits_text)
                    ),
                    onDismiss = { showInfoSheet = false }
                )
            }

            // BackHandler с подтверждением остановки
            BackHandler(enabled = true) {
                confirmStopDialog = true
            }
            if (confirmStopDialog) {
                AlertDialog(
                    onDismissRequest = { confirmStopDialog = false },
                    title = { Text(text = stringResource(id = R.string.stop)) },
                    text = { Text(text = stringResource(id = R.string.breathing_stop)) },
                    confirmButton = {
                        TextButton(onClick = {
                            confirmStopDialog = false
                            viewModel.setEvent(BreathingContract.Event.StopPattern)
                            navController.popBackStack()
                        }) { Text(text = stringResource(id = R.string.common_ok)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { confirmStopDialog = false }) { Text(text = stringResource(id = R.string.common_cancel)) }
                    }
                )
            }
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
                            text = stringResource(id = R.string.breathing_no_patterns),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    // Категории
                    categoryTabs(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Отфильтрованные паттерны
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

                    breathingGrid(
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
}

/**
 * Вкладки категорий дыхательных паттернов.
 * @param selectedCategory Выбранная категория.
 * @param onCategorySelected Обработчик выбора категории.
 */
@Composable
fun categoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val context = LocalContext.current
    val categories = listOf(
        "all" to stringResource(id = R.string.categories),
        "relaxation" to stringResource(id = R.string.breathing_pattern_calm),
        "energy" to stringResource(id = R.string.meditation_category_energy),
        "focus" to stringResource(id = R.string.meditation_category_focus)
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

/**
 * Список дыхательных паттернов.
 * @param patterns Список паттернов.
 * @param onPatternSelected Обработчик выбора паттерна.
 */
@Composable
fun breathingGrid(
    patterns: List<BreathingPattern>,
    onPatternSelected: (BreathingPattern) -> Unit
) {
    // 2 в ряд по умолчанию, на больших экранах — 3
    val columns = if (androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 600) 3 else 2
    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
        columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(patterns.size) { index ->
            val pattern = patterns[index]
            breathingCard(pattern = pattern, onClick = { onPatternSelected(pattern) })
        }
    }
}

/**
 * Карточка дыхательного паттерна.
 * @param pattern Паттерн дыхания.
 * @param onClick Callback при нажатии.
 */
@Composable
fun breathingCard(
    pattern: BreathingPattern,
    onClick: () -> Unit
) {
    // Покачивание
    val infinite = androidx.compose.animation.core.rememberInfiniteTransition(label = "breath_card_sway")
    val sway by infinite.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(3800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "sway"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(
                rotationZ = sway,
                translationY = sway * 2f
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = pattern.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = pattern.description ?: "", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                Spacer(modifier = Modifier.height(8.dp))

                breathingDetails(pattern = pattern)

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(id = R.string.pattern_preview_inhale, pattern.inhaleSeconds, pattern.holdAfterInhaleSeconds), style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(text = stringResource(id = R.string.pattern_preview_exhale, pattern.exhaleSeconds, pattern.holdAfterExhaleSeconds), style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(text = stringResource(id = R.string.pattern_preview_cycles, pattern.totalCycles), style = MaterialTheme.typography.bodySmall, color = Color.White)

                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(id = R.string.start))
                }
            }
        }
    }
}

/**
 * Визуализация схемы дыхательного паттерна.
 * @param pattern Паттерн дыхания.
 */
@Composable
fun breathingDetails(pattern: BreathingPattern) {
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

/**
 * Активная сессия дыхания.
 * @param state Состояние экрана дыхания.
 * @param onStop Callback для остановки.
 */
@Composable
fun ActiveBreathingSession(
    state: BreathingContract.State,
    onStop: () -> Unit,
    onInfo: () -> Unit
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

            Text(text = stringResource(id = R.string.breathing_cycle_message, pattern.totalCycles - state.remainingCycles + 1, pattern.totalCycles), style = MaterialTheme.typography.bodyMedium)
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
        val appCtx = LocalContext.current.applicationContext
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
                // Info button
                IconButton(onClick = onInfo) {
                    Icon(Icons.Default.Info, contentDescription = stringResource(id = R.string.meditation_about))
                }
            Button(
                onClick = onStop,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Stop, contentDescription = stringResource(id = R.string.stop))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(id = R.string.stop))
            }
        }
    }
}

/**
 * Анимированный круг дыхательного паттерна.
 * @param phase Фаза дыхания.
 * @param progress Прогресс анимации.
 */
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
