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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import com.example.spybrain.data.repository.MeditationRepositoryImpl.MeditationTrack
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(
    viewModel: MeditationViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.uiState.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val voiceService = remember { 
        // Создаем простую версию без settingsDataStore для UI
        VoiceAssistantService(context, null)
    }
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
                is MeditationContract.Effect.TrackStarted -> {
                    // Можно добавить уведомление о начале трека
                    Timber.d("Трек начат: ${effect.track.id}")
                }
                is MeditationContract.Effect.TrackCompleted -> {
                    // Можно добавить уведомление о завершении трека
                    Timber.d("Трек завершен: ${effect.track.id}")
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
                state.currentPlaying == null -> {
                    // Показываем выбор между медитациями и треками
                    var selectedMode by remember { mutableStateOf("meditations") }
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Переключатель режимов
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Кнопка "Медитации"
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                                    .clickable { selectedMode = "meditations" },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedMode == "meditations") {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    } else {
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (selectedMode == "meditations") 8.dp else 4.dp
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.meditations),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            
                            // Кнопка "Треки"
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                                    .clickable { selectedMode = "tracks" },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedMode == "tracks") {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    } else {
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (selectedMode == "tracks") 8.dp else 4.dp
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.meditation_tracks_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        
                        // Контент в зависимости от выбранного режима
                        when (selectedMode) {
                            "meditations" -> MeditationList(viewModel, settings.voiceHintsEnabled)
                            "tracks" -> MeditationTrackPlayer(viewModel)
                        }
                    }
                }
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
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
                initialOffsetY = { -50 },
                animationSpec = tween(1000)
            ),
            exit = fadeOut(animationSpec = tween(500)) + slideOutVertically(
                targetOffsetY = { -50 },
                animationSpec = tween(500)
            )
        ) {
            Text(
                text = "Выберите медитацию",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = state.meditations,
                key = { it.id }
            ) { meditation ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                        initialOffsetY = { 100 },
                        animationSpec = tween(800)
                    ),
                    exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                        targetOffsetY = { 100 },
                        animationSpec = tween(300)
                    )
                ) {
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
    
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
            initialOffsetY = { 200 },
            animationSpec = tween(1000)
        ),
        exit = fadeOut(animationSpec = tween(500)) + slideOutVertically(
            targetOffsetY = { 200 },
            animationSpec = tween(500)
        )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Заголовок медитации
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1500)) + slideInVertically(
                    initialOffsetY = { -50 },
                    animationSpec = tween(1500)
                )
            ) {
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
            }
            
            // Визуализация медитации
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(2000)) + slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(2000)
                )
            ) {
                MeditationCircle(isPlaying = isPlaying)
            }
            
            // Прогресс и контроллеры
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(2500)) + slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(2500)
                )
            ) {
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

@Composable
fun MeditationTrackPlayer(
    viewModel: MeditationViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок секции
        Text(
            text = stringResource(id = R.string.meditation_tracks_title),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Список треков
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.meditationTracks) { track ->
                MeditationTrackItem(
                    track = track,
                    isSelected = state.selectedTrack?.id == track.id,
                    isPlaying = state.currentPlayingTrack?.id == track.id && state.isTrackPlaying,
                    onClick = { viewModel.handleEvent(MeditationContract.Event.SelectMeditationTrack(track)) }
                )
            }
        }
        
        // Элементы управления воспроизведением
        if (state.selectedTrack != null) {
            Spacer(modifier = Modifier.height(16.dp))
            TrackPlayerControls(
                track = state.selectedTrack!!,
                isPlaying = state.isTrackPlaying,
                progress = state.trackProgress,
                duration = state.trackDuration,
                currentPosition = state.currentPosition,
                onPlayPause = { 
                    viewModel.handleEvent(MeditationContract.Event.PlaySelectedTrack)
                },
                onNext = { viewModel.handleEvent(MeditationContract.Event.NextTrack) },
                onPrevious = { viewModel.handleEvent(MeditationContract.Event.PreviousTrack) },
                onSeek = { position -> viewModel.handleEvent(MeditationContract.Event.SeekToPosition(position)) }
            )
        }
    }
}

@Composable
fun MeditationTrackItem(
    track: MeditationTrack,
    isSelected: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.7f,
        animationSpec = tween(300),
        label = "track_alpha"
    )
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = tween(300),
        label = "track_scale"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(
                alpha = animatedAlpha,
                scaleX = animatedScale,
                scaleY = animatedScale
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка трека
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    // Анимированная иконка воспроизведения
                    val infiniteTransition = rememberInfiniteTransition(label = "playing_animation")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 0.8f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "playing_scale"
                    )
                    
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(id = R.string.playing),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(id = R.string.play),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Информация о треке
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = track.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                
                Text(
                    text = stringResource(id = R.string.meditation_track_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            
            // Индикатор выбора
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = stringResource(id = R.string.selected),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun TrackPlayerControls(
    track: MeditationTrack,
    isPlaying: Boolean,
    progress: Float,
    duration: Long,
    currentPosition: Long,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Название текущего трека
            Text(
                text = stringResource(id = track.titleRes),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Прогресс-бар
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Slider(
                    value = progress,
                    onValueChange = { newProgress ->
                        val newPosition = (duration * newProgress).toLong()
                        onSeek(newPosition)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(currentPosition),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = formatDuration(duration),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Кнопки управления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка "Предыдущий"
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(id = R.string.previous),
                        tint = Color.White,
                        modifier = Modifier.rotate(180f)
                    )
                }
                
                // Кнопка Play/Pause
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) stringResource(id = R.string.pause) else stringResource(id = R.string.play),
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Кнопка "Следующий"
                IconButton(
                    onClick = onNext,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(id = R.string.next),
                        tint = Color.White
                    )
                }
            }
        }
    }
} 