package com.example.spybrain.presentation.meditation

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spybrain.R
import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.presentation.settings.SettingsViewModel
import com.example.spybrain.service.BackgroundMusicService
import com.example.spybrain.service.VoiceAssistantService
import kotlinx.coroutines.delay
import android.widget.Toast
import androidx.compose.runtime.DisposableEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(
    viewModel: MeditationViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.uiState.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val voiceService = remember { VoiceAssistantService(context) }
    val player = viewModel.player

    // DisposableEffect для очистки ресурсов при размонтировании композабла
    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanupResources()
        }
    }

    // Ambient background audio
    LaunchedEffect(settings.ambientEnabled, settings.ambientTrack) {
        if (settings.ambientEnabled && settings.ambientTrack.isNotEmpty()) {
            try {
                val intent = Intent(context, BackgroundMusicService::class.java).apply {
                    action = BackgroundMusicService.ACTION_PLAY
                    putExtra(BackgroundMusicService.EXTRA_URL, "https://example.com/audio/${settings.ambientTrack}.mp3")
                }
                context.startService(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Не удалось запустить фоновую музыку", Toast.LENGTH_SHORT).show()
            }
        } else {
            try {
                val intent = Intent(context, BackgroundMusicService::class.java).apply {
                    action = BackgroundMusicService.ACTION_STOP
                }
                context.startService(intent)
            } catch (e: Exception) {
                // Игнорируем ошибки при остановке
            }
        }
    }

    // Обработка эффектов от ViewModel, включая голосовые подсказки
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is MeditationContract.Effect.ShowError -> {
                    Toast.makeText(context, effect.error.toString(), Toast.LENGTH_SHORT).show()
                }
                is MeditationContract.Effect.Speak -> {
                    if (settings.voiceHintsEnabled) {
                        try {
                            voiceService.speak(effect.text)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Ошибка голосовой подсказки", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Медитация") }
            )
        }
    ) { paddingValues ->
        // Заменяем Crossfade на простой Box с key для пересоздания при смене темы
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            val theme = settings.theme
            
            // Определяем ресурсы фона и иконки без использования remember
            val bgPainter = when (theme) {
                "water" -> painterResource(id = R.drawable.bg_water)
                "space" -> painterResource(id = R.drawable.bg_space)
                "nature" -> painterResource(id = R.drawable.bg_nature)
                "air" -> painterResource(id = R.drawable.bg_air)
                else -> painterResource(id = R.drawable.bg_nature)
            }
            
            val themeIcon = when (theme) {
                "water" -> painterResource(id = R.drawable.ic_water)
                "space" -> painterResource(id = R.drawable.ic_space)
                "nature" -> painterResource(id = R.drawable.ic_nature)
                "air" -> painterResource(id = R.drawable.ic_air)
                else -> painterResource(id = R.drawable.ic_nature)
            }
            
            Image(
                painter = bgPainter,
                contentDescription = stringResource(id = R.string.meditation_background_image_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            
            // Полупрозрачный оверлей для лучшей читаемости
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            
            // Иконка темы
            Icon(
                painter = themeIcon,
                contentDescription = theme,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                tint = Color.Unspecified
            )
            
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.currentPlaying == null -> MeditationList(viewModel, settings.voiceHintsEnabled)
                else -> MeditationPlayerUI(viewModel, state, player, settings.voiceHintsEnabled)
            }
        }
    }
}

@Composable
fun MeditationList(
    viewModel: MeditationViewModel,
    voiceHintsEnabled: Boolean
) {
    val state by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Выберите медитацию",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.meditations) { meditation ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setEvent(MeditationContract.Event.PlayMeditation(meditation))
                            
                            if (voiceHintsEnabled) {
                                viewModel.setEvent(MeditationContract.Event.VoiceCommand("начать медитацию ${meditation.title}"))
                            }
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(text = meditation.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = meditation.description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.duration_format, meditation.durationMinutes),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = meditation.category,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MeditationPlayerUI(
    viewModel: MeditationViewModel,
    uiState: MeditationContract.State,
    player: com.example.spybrain.domain.service.IPlayerService,
    voiceHintsEnabled: Boolean
) {
    val meditation = uiState.currentPlaying ?: return
    var position by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var isPlaying by remember { mutableStateOf(true) }
    
    // Обновляем позицию проигрывания
    LaunchedEffect(player) {
        while (true) {
            position = player.getCurrentPosition()
            duration = player.getDuration().takeIf { it > 0 } ?: 0L
            isPlaying = player.isPlaying()
            delay(500L)
        }
    }
    
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок медитации
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 30.dp)
        ) {
            Text(
                text = meditation.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = meditation.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
        
        // Визуализация медитации
        MeditationCircle(isPlaying = isPlaying)
        
        // Прогресс и контроллеры
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Время
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(position),
                    color = Color.White
                )
                Text(
                    text = formatDuration(duration),
                    color = Color.White
                )
            }
            
            // Прогресс-бар
            Slider(
                value = if (duration > 0) position / duration.toFloat() else 0f,
                onValueChange = { frac -> player.seekTo((duration * frac).toLong()) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Кнопки управления
            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Левая кнопка - Стоп
                StopButton { viewModel.setEvent(MeditationContract.Event.StopMeditation) }
                
                // Центральная кнопка - Играть/Пауза
                PlayPauseButton(
                    isPlaying = isPlaying,
                    onPlayPause = {
                        if (isPlaying) {
                            viewModel.setEvent(MeditationContract.Event.PauseMeditation)
                        } else {
                            uiState.currentPlaying?.let { 
                                viewModel.setEvent(MeditationContract.Event.PlayMeditation(it)) 
                            }
                        }
                    }
                )
                
                // Правая кнопка - Ещё
                ExtraButton()
            }
        }
    }
}

@Composable
fun MeditationCircle(isPlaying: Boolean) {
    // Анимация пульсации
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val animationValue by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAnimation"
    )
    
    val size = if (isPlaying) animationValue else 0.7f
    val color = MaterialTheme.colorScheme.primary
    
    Box(
        modifier = Modifier.size(250.dp),
        contentAlignment = Alignment.Center
    ) {
        // Градиентный круг
        Canvas(modifier = Modifier.matchParentSize()) {
            val centerX = this.size.width / 2f
            val centerY = this.size.height / 2f
            val radius = (this.size.width.coerceAtMost(this.size.height) / 2f) * size
            
            // Градиентное заполнение
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.7f),
                        color.copy(alpha = 0.2f)
                    ),
                    center = Offset(centerX, centerY),
                    radius = radius
                ),
                radius = radius,
                center = Offset(centerX, centerY)
            )
            
            // Контур
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Composable
fun StopButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Stop,
            contentDescription = stringResource(id = R.string.meditation_stop),
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun PlayPauseButton(isPlaying: Boolean, onPlayPause: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            .clickable(onClick = onPlayPause),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) 
                stringResource(id = R.string.meditation_pause)
            else 
                stringResource(id = R.string.meditation_play),
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun ExtraButton() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { /* Дополнительное действие */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_nature),
            contentDescription = "Дополнительное действие",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}

// Форматирование времени в формат MM:SS
private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
} 