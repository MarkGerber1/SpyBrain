package com.example.spybrain.presentation.reminders

// TODO: Сбор всех TODO/FIXME по файлу ниже

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spybrain.R
import com.example.spybrain.data.storage.model.HeartRateMeasurement
import com.example.spybrain.presentation.reminders.HeartRateContract
import com.example.spybrain.presentation.reminders.HeartRateViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spybrain.presentation.reminders.HeartRateContract
import com.example.spybrain.presentation.reminders.HeartRateViewModel

/**
 * @param navController РљРѕРЅС‚СЂРѕР»Р»РµСЂ РЅР°РІРёРіР°С†РёРё.
 * @param viewModel ViewModel РїСѓР»СЊСЃРѕРјРµС‚СЂР°.
 */
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
                    // РџРѕРєР°Р·С‹РІР°РµРј toast СЃРѕРѕР±С‰РµРЅРёРµ
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
                // РђРЅРёРјРёСЂРѕРІР°РЅРЅРѕРµ СЃРµСЂРґС†Рµ
                AnimatedHeart(
                    isBeating = state.isMeasuring,
                    heartRate = state.currentHeartRate,
                    modifier = Modifier.size(200.dp)
                )
            }

            item {
                // РРЅС„РѕСЂРјР°С†РёСЏ Рѕ РїСѓР»СЊСЃРµ
                HeartRateInfo(
                    heartRate = state.currentHeartRate,
                    isMeasuring = state.isMeasuring,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // РљРЅРѕРїРєР° РёР·РјРµСЂРµРЅРёСЏ
                HeartRateButton(
                    isMeasuring = state.isMeasuring,
                    onMeasureClick = { viewModel.setEvent(HeartRateContract.Event.StartMeasurement) },
                    onStopClick = { viewModel.setEvent(HeartRateContract.Event.StopMeasurement) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // РњРѕС‚РёРІР°С†РёРѕРЅРЅС‹Рµ Р±Р°Р»Р»С‹
                MotivationalPoints(
                    points = state.motivationalPoints,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // Р“СЂР°С„РёРє РїСѓР»СЊСЃР°
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

/**
 * @param isBeating РџСЂРёР·РЅР°Рє Р°РЅРёРјР°С†РёРё.
 * @param heartRate РўРµРєСѓС‰РёР№ РїСѓР»СЊСЃ.
 * @param modifier РњРѕРґРёС„РёРєР°С‚РѕСЂ Compose.
 */
@Composable
fun AnimatedHeart(
    isBeating: Boolean,
    heartRate: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heart")
    val scale by infiniteTransition.animateFloatAsState(
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

/**
 * @param color Р¦РІРµС‚.
 * @param size Р Р°Р·РјРµСЂ.
 */
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

/**
 * @param heartRate РўРµРєСѓС‰РёР№ РїСѓР»СЊСЃ.
 * @param isMeasuring РџСЂРёР·РЅР°Рє РёР·РјРµСЂРµРЅРёСЏ.
 * @param modifier РњРѕРґРёС„РёРєР°С‚РѕСЂ Compose.
 */
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
                text = if (isMeasuring) stringResource(R.string.heart_rate_measuring) else "Р“РѕС‚РѕРІ Рє РёР·РјРµСЂРµРЅРёСЋ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * @param isMeasuring РџСЂРёР·РЅР°Рє РёР·РјРµСЂРµРЅРёСЏ.
 * @param onMeasureClick Callback СЃС‚Р°СЂС‚Р°.
 * @param onStopClick Callback РѕСЃС‚Р°РЅРѕРІРєРё.
 * @param modifier РњРѕРґРёС„РёРєР°С‚РѕСЂ Compose.
 */
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

/**
 * @param points РљРѕР»РёС‡РµСЃС‚РІРѕ Р±Р°Р»Р»РѕРІ.
 * @param modifier РњРѕРґРёС„РёРєР°С‚РѕСЂ Compose.
 */
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

/**
 * @param measurements РЎРїРёСЃРѕРє РёР·РјРµСЂРµРЅРёР№.
 * @param modifier РњРѕРґРёС„РёРєР°С‚РѕСЂ Compose.
 */
@Composable
fun HeartRateGraph(
    measurements: List<HeartRateMeasurement>,
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

/**
 * @param measurements РЎРїРёСЃРѕРє РёР·РјРµСЂРµРЅРёР№.
 * @param size Р Р°Р·РјРµСЂ.
 */
fun DrawScope.drawHeartRateGraph(measurements: List<HeartRateMeasurement>, size: androidx.compose.ui.geometry.Size) {
    if (measurements.isEmpty()) return

    val maxBpm = measurements.maxOf { it.heartRate }
    val minBpm = measurements.minOf { it.heartRate }
    val range = maxBpm - minBpm

    val paint = Paint().apply {
        color = Color.Red
        strokeWidth = 3f
        style = PaintingStyle.Stroke
    }

    val path = Path()
    val stepX = size.width / (measurements.size - 1)

    measurements.forEachIndexed { index, measurement ->
        val x = index * stepX
        val normalizedBpm = (measurement.heartRate - minBpm).toFloat() / range
        val y = size.height - (normalizedBpm * size.height)

        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }

        // Р РёСЃСўРµРј СЃРµСЂРґРµС‡РєРё РІ С‚РѕС‡РєР°С…
        drawCircle(
            color = Color.Red,
            radius = 4f,
            center = Offset(x, y)
        )
    }

    drawPath(path, Color.Red, style = Stroke(width = 3f))
}

/**
 * @param modifier РњРѕРґРёС„РёРєР°С‚РѕСЂ Compose.
 */
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
