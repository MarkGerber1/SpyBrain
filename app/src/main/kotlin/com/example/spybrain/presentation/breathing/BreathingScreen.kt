package com.example.spybrain.presentation.breathing

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spybrain.voice.VoiceAssistantService
import com.example.spybrain.domain.model.BreathingPattern
import com.example.spybrain.presentation.breathing.BreathingContract
import com.example.spybrain.presentation.breathing.BreathingViewModel
import com.example.spybrain.presentation.breathing.PulseAnalyzer
import com.example.spybrain.presentation.breathing.components.HeartBeatAnimation
import com.example.spybrain.presentation.breathing.components.EcgGraph
import com.example.spybrain.presentation.navigation.Screen
import com.example.spybrain.presentation.settings.SettingsViewModel
import com.example.spybrain.presentation.components.Ticker
import androidx.compose.animation.Crossfade
import com.example.spybrain.util.UiError
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.Composable  // FIXME билд-фикс 09.05.2025
import com.example.spybrain.presentation.settings.SettingsContract.State as SettingsState  // FIXME билд-фикс 09.05.2025

@Composable
fun BreathingScreen(
    navController: NavHostController,
    viewModel: BreathingViewModel = hiltViewModel()
) { // FIXME билд-фикс 09.05.2025: Composable context
    // TODO: Провести ревизию UI/UX: добавить анимации переходов, улучшить визуальное разнообразие, проверить доступность (контрастность, размеры шрифтов)
    // TODO Composable-контекст: убедиться, что все вызовы @Composable только внутри @Composable-функций // FIXME билд-фикс 09.05.2025
    // Settings for voice enabled
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settingsState by settingsViewModel.uiState.collectAsState() as State<SettingsState>
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    // Сервис для голосовых подсказок
    val voiceService = remember { VoiceAssistantService(context) }
    // Биометрический анализ пульса
    val pulseAnalyzer = remember { PulseAnalyzer() }
    var measuring by remember { mutableStateOf(false) }
    val bpm by pulseAnalyzer.bpmFlow.collectAsState()
    // Запуск/остановка измерений
    LaunchedEffect(measuring) {
        if (measuring) pulseAnalyzer.start() else pulseAnalyzer.stop()
    }

    // Обработка эффектов из ViewModel
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BreathingContract.Effect.ShowError ->
                    Toast.makeText(context, when(val err = effect.error) {
                        is UiError.Custom -> err.message
                        is UiError.NetworkError -> stringResource(id = com.example.spybrain.R.string.error_network)
                        is UiError.ValidationError -> stringResource(id = com.example.spybrain.R.string.error_validation)
                        is UiError.UnknownError -> stringResource(id = com.example.spybrain.R.string.error_unknown)
                        else -> stringResource(id = com.example.spybrain.R.string.error_general)
                    }, Toast.LENGTH_SHORT).show()
                is BreathingContract.Effect.Vibrate -> {
                    // TODO: добавить haptic feedback
                }
                is BreathingContract.Effect.Speak -> {
                    // воспроизведение речи через сервис, если включено
                    if (settingsState.value.voiceHintsEnabled) {
                        voiceService.speak(effect.text)
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            state.currentPattern == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { navController.navigate(Screen.PatternBuilder.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = com.example.spybrain.R.string.breathing_create_pattern)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = com.example.spybrain.R.string.breathing_create_pattern))
                    }
                    // FIXME билд-фикс 09.05.2025: явный тип лямбды, внутри @Composable
                    BreathingPatternList(state) { idx: Int ->
                        viewModel.setEvent(
                            BreathingContract.Event.StartPattern(state.patterns[idx])
                        )
                    }
                }
            }
            else -> {
                // Экран выполнения практики
                BreathingPracticeContent(
                    state = state,
                    settingsState = settingsState,
                    measuring = measuring,
                    onToggleMeasuring = { measuring = !measuring },
                    bpm = bpm,
                    viewModel = viewModel
                ) // FIXME билд-фикс 09.05.2025: Composable context
            }
        }
    }
}

// FIXME билд-фикс 09.05.2025: Composable вне контекста
@Composable
private fun BreathingPatternList(
    state: BreathingContract.State,
    onPatternClick: (Int) -> Unit
) {
    val patternIcons = List(state.patterns.size) { Icons.Default.SelfImprovement } // TODO: заменить на реальные иконки паттернов
    val patternLabels = state.patterns.map { it.name }
    com.example.spybrain.presentation.components.IconMenuGrid(
        icons = patternIcons,
        labels = patternLabels,
        onClick = onPatternClick
    )
}

@Composable
private fun BreathingPracticeContent(
    state: BreathingContract.State,
    settingsState: SettingsState,
    measuring: Boolean,
    onToggleMeasuring: () -> Unit,
    bpm: Int,
    viewModel: BreathingViewModel
) {
    Crossfade(targetState = settingsState.value.theme) { theme ->
        val bgRes = when (theme) {
            "water" -> com.example.spybrain.R.drawable.bg_water
            "space" -> com.example.spybrain.R.drawable.bg_space
            "nature" -> com.example.spybrain.R.drawable.bg_nature
            "air" -> com.example.spybrain.R.drawable.bg_air
            else -> com.example.spybrain.R.drawable.bg_nature
        }
        val iconRes = when (theme) {
            "water" -> com.example.spybrain.R.drawable.ic_water
            "space" -> com.example.spybrain.R.drawable.ic_space
            "nature" -> com.example.spybrain.R.drawable.ic_nature
            "air" -> com.example.spybrain.R.drawable.ic_air
            else -> com.example.spybrain.R.drawable.ic_nature
        }
        Box(modifier = Modifier.fillMaxSize()) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = bgRes),
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                tint = Color.Unspecified
            )
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // Кнопка замера пульса и отображение BPM
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onToggleMeasuring) {
                        Text(if (measuring) stringResource(id = com.example.spybrain.R.string.breathing_stop_measuring) else stringResource(id = com.example.spybrain.R.string.breathing_measure_pulse))
                    }
                    Text("BPM: $bpm", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Анимация сердца
                HeartBeatAnimation(bpm = bpm, size = 100.dp)
                Spacer(modifier = Modifier.height(16.dp))
                // ECG-график
                EcgGraph(modifier = Modifier.size(200.dp, 100.dp))
                Spacer(modifier = Modifier.height(8.dp))
                // Бегущая строка с рекомендациями от ViewModel
                val advice = when (viewModel.analyzeBpm(bpm)) {
                    BreathingViewModel.BpmLevel.LOW -> stringResource(id = com.example.spybrain.R.string.breathing_advice_low_bpm)
                    BreathingViewModel.BpmLevel.NORMAL -> stringResource(id = com.example.spybrain.R.string.breathing_advice_normal_bpm)
                    BreathingViewModel.BpmLevel.HIGH -> stringResource(id = com.example.spybrain.R.string.breathing_advice_high_bpm)
                    BreathingViewModel.BpmLevel.CRITICAL -> stringResource(id = com.example.spybrain.R.string.breathing_advice_critical_bpm)
                }
                Text(advice, style = MaterialTheme.typography.bodyMedium)
                // Динамические советы от HealthAdvisor
                val dynamicText = remember(state.currentPhase, bpm) {
                    viewModel.getDynamicAdvices(state.currentPhase!!, bpm)
                }
                Ticker(text = dynamicText, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
                Spacer(modifier = Modifier.height(16.dp))
                // Дальнейшая логика практики
                val progress = state.cycleProgress
                val scale by animateFloatAsState(
                    targetValue = 0.5f + 0.5f * progress,
                    animationSpec = infiniteRepeatable(animation = tween(800))
                )
                val circleColor = MaterialTheme.colorScheme.primary
                Canvas(modifier = Modifier.size((200 * scale).dp)) { drawCircle(color = circleColor) }
                Text(when (state.currentPhase) {
                    BreathingContract.BreathingPhase.Inhale -> stringResource(id = com.example.spybrain.R.string.breathing_inhale)
                    BreathingContract.BreathingPhase.HoldAfterInhale -> stringResource(id = com.example.spybrain.R.string.breathing_hold)
                    BreathingContract.BreathingPhase.Exhale -> stringResource(id = com.example.spybrain.R.string.breathing_exhale)
                    BreathingContract.BreathingPhase.HoldAfterExhale -> stringResource(id = com.example.spybrain.R.string.breathing_phase_rest)
                    else -> ""
                }, style = MaterialTheme.typography.titleLarge)
                Text("Осталось циклов: ${state.remainingCycles}", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { viewModel.setEvent(BreathingContract.Event.StopPattern) }) { Text(stringResource(id = com.example.spybrain.R.string.stop)) }
                    IconButton(onClick = { viewModel.startListeningVoice() }) { Icon(Icons.Filled.Mic, contentDescription = stringResource(id = com.example.spybrain.R.string.breathing_voice_button)) }
                }
            }
        }
    }
} 