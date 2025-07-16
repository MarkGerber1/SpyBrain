package com.example.spybrain.presentation.breathing

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import com.example.spybrain.util.VibrationUtil
import androidx.compose.ui.res.stringResource
import com.example.spybrain.R
import com.example.spybrain.presentation.breathing.BreathingContract.BreathingPhase

/**
 * Р­РєСЂР°РЅ РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїСЂР°РєС‚РёРє.
 * @param navController РљРѕРЅС‚СЂРѕР»Р»РµСЂ РЅР°РІРёРіР°С†РёРё.
 * @param viewModel ViewModel РґР»СЏ СѓРїСЂР°РІР»РµРЅРёСЏ СЃРѕСЃС‚РѕСЏРЅРёРµРј СЌРєСЂР°РЅР°.
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

    // Р­С„С„РµРєС‚ РґР»СЏ РѕР±СЂР°Р±РѕС‚РєРё СѓРІРµРґРѕРјР»РµРЅРёР№ Рё РѕС€РёР±РѕРє
    LaunchedEffect(key1 = viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BreathingContract.Effect.ShowError -> {
                    VibrationUtil.longVibration(context)
                    Toast.makeText(context, effect.error.toString(), Toast.LENGTH_SHORT).show()
                }
                is BreathingContract.Effect.Vibrate -> {
                    // Р’РёР±СЂР°С†РёСЏ РїСЂРё СЃРјРµРЅРµ С„Р°Р·С‹ РґС‹С…Р°РЅРёСЏ
                    VibrationUtil.breathingVibration(context)
                }
                is BreathingContract.Effect.Speak -> {
                    // Р“РѕР»РѕСЃРѕРІР°СЏ РїРѕРґСЃРєР°Р·РєР°
                    Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Р”С‹С…Р°С‚РµР»СЊРЅС‹Рµ РїСЂР°РєС‚РёРєРё") },
                actions = {
                    // РљРЅРѕРїРєР° РґР»СЏ СЃРѕР·РґР°РЅРёСЏ СЃРѕР±СЃС‚РІРµРЅРЅРѕРіРѕ С€Р°Р±Р»РѕРЅР°
                    IconButton(onClick = {
                        VibrationUtil.shortVibration(context)
                        navController.navigate("pattern_builder")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "РЎРѕР·РґР°С‚СЊ С€Р°Р±Р»РѕРЅ")
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
            // РђРєС‚РёРІРЅР°СЏ СЃРµСЃСЃРёСЏ РґС‹С…Р°РЅРёСЏ
            ActiveBreathingSession(
                state = state,
                onStop = { viewModel.setEvent(BreathingContract.Event.StopPattern) }
            )
        } else {
            // РЎРїРёСЃРѕРє С€Р°Р±Р»РѕРЅРѕРІ СЃ РєР°С‚РµРіРѕСЂРёСЏРјРё
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
                            text = "РќРµС‚ РґРѕСЃС‚СѓРїРЅС‹С… С€Р°Р±Р»РѕРЅРѕРІ РґС‹С…Р°РЅРёСЏ",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    // РљР°С‚РµРіРѕСЂРёРё
                    categoryTabs(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Р¤РёР»СЊС‚СЂРѕРІР°РЅРЅС‹Рµ РїР°С‚С‚РµСЂРЅС‹
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

                    breathingList(
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

/**
 * Р’РєР»Р°РґРєРё РєР°С‚РµРіРѕСЂРёР№ РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
 * @param selectedCategory Р’С‹Р±СЂР°РЅРЅР°СЏ РєР°С‚РµРіРѕСЂРёСЏ.
 * @param onCategorySelected РћР±СЂР°Р±РѕС‚С‡РёРє РІС‹Р±РѕСЂР° РєР°С‚РµРіРѕСЂРёРё.
 */
@Composable
fun categoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val context = LocalContext.current
    val categories = listOf(
        "all" to "Р’СЃРµ",
        "relaxation" to "Р Р°СЃСЃР»Р°Р±Р»РµРЅРёРµ",
        "energy" to "Р­РЅРµСЂРіРёСЏ",
        "focus" to "РљРѕРЅС†РµРЅС‚СЂР°С†РёСЏ"
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
 * РЎРїРёСЃРѕРє РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїР°С‚С‚РµСЂРЅРѕРІ.
 * @param patterns РЎРїРёСЃРѕРє РїР°С‚С‚РµСЂРЅРѕРІ.
 * @param onPatternSelected РћР±СЂР°Р±РѕС‚С‡РёРє РІС‹Р±РѕСЂР° РїР°С‚С‚РµСЂРЅР°.
 */
@Composable
fun breathingList(
    patterns: List<BreathingPattern>,
    onPatternSelected: (BreathingPattern) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(patterns) { pattern ->
            breathingItem(pattern = pattern, onClick = { onPatternSelected(pattern) })
        }
    }
}

/**
 * РљР°СЂС‚РѕС‡РєР° РґС‹С…Р°С‚РµР»СЊРЅРѕРіРѕ РїР°С‚С‚РµСЂРЅР°.
 * @param pattern РџР°С‚С‚РµСЂРЅ РґС‹С…Р°РЅРёСЏ.
 * @param onClick Callback РїСЂРё РЅР°Р¶Р°С‚РёРё.
 */
@Composable
fun breathingItem(
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
                text = pattern.description ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Р’РёР·СѓР°Р»РёР·Р°С†РёСЏ СЃС…РµРјС‹ РґС‹С…Р°РЅРёСЏ
            breathingDetails(pattern = pattern)

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Р’РґРѕС…: ${pattern.inhaleSeconds}СЃ вЂў Р—Р°РґРµСЂР¶РєР°: ${pattern.holdAfterInhaleSeconds}СЃ вЂў " +
                      "Р’С‹РґРѕС…: ${pattern.exhaleSeconds}СЃ вЂў Р—Р°РґРµСЂР¶РєР°: ${pattern.holdAfterExhaleSeconds}СЃ",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Р¦РёРєР»РѕРІ: ${pattern.totalCycles}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Р’РёР·СѓР°Р»РёР·Р°С†РёСЏ СЃС…РµРјС‹ РґС‹С…Р°С‚РµР»СЊРЅРѕРіРѕ РїР°С‚С‚РµСЂРЅР°.
 * @param pattern РџР°С‚С‚РµСЂРЅ РґС‹С…Р°РЅРёСЏ.
 */
@Composable
fun breathingDetails(pattern: BreathingPattern) {
    val totalDuration = pattern.inhaleSeconds + pattern.holdAfterInhaleSeconds +
                         pattern.exhaleSeconds + pattern.holdAfterExhaleSeconds

    // РџСЂРѕРІРµСЂСЏРµРј, С‡С‚Рѕ РѕР±С‰Р°СЏ РґР»РёС‚РµР»СЊРЅРѕСЃС‚СЊ Р±РѕР»СЊС€Рµ 0, РёРЅР°С‡Рµ РёСЃРїРѕР»СЊР·СѓРµРј fallback
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
 * РђРєС‚РёРІРЅР°СЏ СЃРµСЃСЃРёСЏ РґС‹С…Р°РЅРёСЏ.
 * @param state РЎРѕСЃС‚РѕСЏРЅРёРµ СЌРєСЂР°РЅР° РґС‹С…Р°РЅРёСЏ.
 * @param onStop Callback РґР»СЏ РѕСЃС‚Р°РЅРѕРІРєРё.
 */
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
        // Р’РµСЂС…РЅСЏСЏ С‡Р°СЃС‚СЊ СЃ РёРЅС„РѕСЂРјР°С†РёРµР№ Рѕ С‚РµРєСѓС‰РµРј С€Р°Р±Р»РѕРЅРµ
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
                text = "Р¦РёРєР» ${pattern.totalCycles - state.remainingCycles + 1} РёР· ${pattern.totalCycles}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // РђРЅРёРјР°С†РёСЏ РґС‹С…Р°РЅРёСЏ
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedBreathCircle(
                phase = state.currentPhase,
                progress = progress
            )
        }

        // РљРЅРѕРїРєРё СѓРїСЂР°РІР»РµРЅРёСЏ
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
                Icon(Icons.Default.Stop, contentDescription = "РћСЃС‚Р°РЅРѕРІРёС‚СЊ")
                Spacer(modifier = Modifier.width(4.dp))
                Text("РћСЃС‚Р°РЅРѕРІРёС‚СЊ")
            }
        }
    }
}

/**
 * РђРЅРёРјРёСЂРѕРІР°РЅРЅС‹Р№ РєСЂСѓРі РґС‹С…Р°С‚РµР»СЊРЅРѕРіРѕ РїР°С‚С‚РµСЂРЅР°.
 * @param phase Р¤Р°Р·Р° РґС‹С…Р°РЅРёСЏ.
 * @param progress РџСЂРѕРіСЂРµСЃСЃ Р°РЅРёРјР°С†РёРё.
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
        // Р¤РѕРЅРѕРІС‹Р№ РєСЂСѓРі
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxSize = minOf(size.width, size.height)
            val radius = (maxSize / 2 * 0.9).toFloat()

            // Р’РЅРµС€РЅРёР№ РєСЂСѓРі (РєРѕРЅС‚СѓСЂ)
            drawCircle(
                color = color.copy(alpha = 0.3f),
                radius = radius,
                center = Offset(x = centerX, y = centerY),
                style = Stroke(width = 8.dp.toPx())
            )

            // РџСЂРѕРіСЂРµСЃСЃ
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

        // Р’РЅСѓС‚СЂРµРЅРЅРёР№ Р°РЅРёРјРёСЂРѕРІР°РЅРЅС‹Р№ РєСЂСѓРі
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
