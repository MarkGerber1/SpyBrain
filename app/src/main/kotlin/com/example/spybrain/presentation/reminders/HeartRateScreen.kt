package com.example.spybrain.presentation.reminders

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import com.example.spybrain.presentation.reminders.HeartRateContract
import com.example.spybrain.presentation.reminders.HeartRateViewModel
import kotlinx.coroutines.delay
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartRateScreen(
    navController: NavHostController,
    viewModel: HeartRateViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HeartRateContract.Effect.ShowToast -> {
                    // Показываем toast сообщение
                }
                is HeartRateContract.Effect.NavigateTo -> {
                    navController.navigate(effect.screen.route)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.heart_rate_title)) },
                actions = {
                    IconButton(onClick = { viewModel.setEvent(HeartRateContract.Event.ShowHistory) }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = stringResource(R.string.heart_rate_history)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                // Анимированное сердце
                AnimatedHeart(
                    isBeating = state.isMeasuring,
                    heartRate = state.currentHeartRate,
                    modifier = Modifier.size(200.dp)
                )
            }

            item {
                // Информация о пульсе
                HeartRateInfo(
                    heartRate = state.currentHeartRate,
                    isMeasuring = state.isMeasuring,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // Кнопка измерения
                HeartRateButton(
                    isMeasuring = state.isMeasuring,
                    onMeasureClick = { viewModel.setEvent(HeartRateContract.Event.StartMeasurement) },
                    onStopClick = { viewModel.setEvent(HeartRateContract.Event.StopMeasurement) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // Мотивационные баллы
                MotivationalPoints(
                    points = state.motivationalPoints,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // График пульса
                HeartRateGraph(
                    measurements = state.measurementHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            if (state.showNewExerciseUnlocked) {
                item {
                    NewExerciseUnlockedCard(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedHeart(
    isBeating: Boolean,
    heartRate: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heart")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isBeating) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (heartRate > 0) (60000 / heartRate).toInt() else 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart_scale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
        ) {
            drawHeart(
                color = Color.Red,
                size = size
            )
        }
        
        if (isBeating) {
            Text(
                text = stringResource(R.string.heart_rate_measuring_pulse),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

fun DrawScope.drawHeart(color: Color, size: androidx.compose.ui.geometry.Size) {
    val path = Path()
    val width = size.width
    val height = size.height
    
    path.moveTo(width / 2, height * 0.3f)
    path.cubicTo(
        width * 0.2f, height * 0.1f,
        width * 0.1f, height * 0.6f,
        width / 2, height * 0.8f
    )
    path.cubicTo(
        width * 0.9f, height * 0.6f,
        width * 0.8f, height * 0.1f,
        width / 2, height * 0.3f
    )
    
    drawPath(path, color, style = Stroke(width = 3f))
}

@Composable
fun HeartRateInfo(
    heartRate: Int,
    isMeasuring: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (heartRate > 0) stringResource(R.string.heart_rate_bpm, heartRate) else "---",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (isMeasuring) stringResource(R.string.heart_rate_measuring) else "Готов к измерению",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HeartRateButton(
    isMeasuring: Boolean,
    onMeasureClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = if (isMeasuring) onStopClick else onMeasureClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isMeasuring) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = if (isMeasuring) Icons.Default.Stop else Icons.Default.Camera,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isMeasuring) stringResource(R.string.heart_rate_stop) else stringResource(R.string.heart_rate_measure),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun MotivationalPoints(
    points: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.heart_rate_motivational_points, points),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun HeartRateGraph(
    measurements: List<Int>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.heart_rate_history),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (measurements.isEmpty()) {
                Text(
                    text = stringResource(R.string.heart_rate_no_history),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    drawHeartRateGraph(measurements, size)
                }
            }
        }
    }
}

fun DrawScope.drawHeartRateGraph(measurements: List<Int>, size: androidx.compose.ui.geometry.Size) {
    if (measurements.isEmpty()) return
    
    val maxBpm = measurements.maxOrNull() ?: 100
    val minBpm = measurements.minOrNull() ?: 60
    val range = maxBpm - minBpm
    
    val paint = Paint().apply {
        color = Color.Red
        strokeWidth = 3f
        style = PaintingStyle.Stroke
    }
    
    val path = Path()
    val stepX = size.width / (measurements.size - 1)
    
    measurements.forEachIndexed { index, bpm ->
        val x = index * stepX
        val normalizedBpm = (bpm - minBpm).toFloat() / range
        val y = size.height - (normalizedBpm * size.height)
        
        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
        
        // Рисуем сердечки в точках
        drawCircle(
            color = Color.Red,
            radius = 4f,
            center = Offset(x, y)
        )
    }
    
    drawPath(path, Color.Red, style = Stroke(width = 3f))
}

@Composable
fun NewExerciseUnlockedCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.heart_rate_new_exercise_unlocked),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = stringResource(R.string.heart_rate_points_for_session),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
} 