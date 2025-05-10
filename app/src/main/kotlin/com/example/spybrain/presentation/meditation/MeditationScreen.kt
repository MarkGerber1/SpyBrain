package com.example.spybrain.presentation.meditation

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.presentation.meditation.MeditationContract
import com.example.spybrain.presentation.meditation.MeditationViewModel
import com.example.spybrain.presentation.settings.SettingsViewModel
import com.example.spybrain.service.MeditationPlayerService
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.spybrain.R
import com.example.spybrain.voice.VoiceAssistantService
import com.example.spybrain.service.BackgroundMusicService
import androidx.compose.ui.res.stringResource

@Composable
fun MeditationScreen(
    viewModel: MeditationViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) { // TODO: Провести ревизию UI/UX: добавить анимации переходов, улучшить визуальное разнообразие, проверить доступность (контрастность, размеры шрифтов)
    val settings by settingsViewModel.uiState.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val voiceService = remember { VoiceAssistantService(context) }
    val player = viewModel.player

    // Ambient background audio
    // FIXME билд-фикс 09.05.2025: временно закомментированы StartAmbientMusic, StopAmbientMusic, Speak, text для устранения ошибки сборки
    // LaunchedEffect(settings.ambientEnabled, settings.ambientTrack) {
    //     if (settings.ambientEnabled && settings.ambientTrack.isNotEmpty()) {
    //         viewModel.setEvent(MeditationContract.Event.StartAmbientMusic(settings.ambientTrack))
    //     } else {
    //         viewModel.setEvent(MeditationContract.Event.StopAmbientMusic)
    //     }
    // }

    // Обработка эффектов от ViewModel, включая голосовые подсказки
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is MeditationContract.Effect.ShowError -> {
                    // TODO: Обработка ошибки (например, Toast или SnackBar)
                }
                is MeditationContract.Effect.Speak -> {
                    // FIXME билд-фикс 09.05.2025: временно закомментированы или исправлены вызовы Speak/text для устранения ошибки сборки
                    // Воспроизведение речи через локальный сервис, если включено
                    // if (settings.voiceHintsEnabled) {
                    //     voiceService.speak(effect.text)
                    // }
                }
            }
        }
    }

    // Smooth background crossfade per theme
    Crossfade(targetState = settings.theme) { theme ->
        val bgPainter = when (theme) {
            "water" -> painterResource(id = R.drawable.bg_water)
            "space" -> painterResource(id = R.drawable.bg_space)
            "nature" -> painterResource(id = R.drawable.bg_nature)
            "air" -> painterResource(id = R.drawable.bg_air)
            else -> painterResource(id = R.drawable.bg_nature)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = bgPainter,
                contentDescription = stringResource(id = com.example.spybrain.R.string.meditation_background_image_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            // Иконка темы
            Icon(
                painter = when (theme) {
                    "water" -> painterResource(id = R.drawable.ic_water)
                    "space" -> painterResource(id = R.drawable.ic_space)
                    "nature" -> painterResource(id = R.drawable.ic_nature)
                    "air" -> painterResource(id = R.drawable.ic_air)
                    else -> painterResource(id = R.drawable.ic_nature)
                },
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
    val context = LocalContext.current
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.meditations) { meditation ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setEvent(MeditationContract.Event.PlayMeditation(meditation))
                    },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = meditation.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(text = stringResource(id = com.example.spybrain.R.string.duration_format, meditation.durationMinutes), style = MaterialTheme.typography.bodySmall)
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
    var position by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    LaunchedEffect(player) {
        while (true) {
            position = player.getCurrentPosition()
            duration = player.getDuration().takeIf { it > 0 } ?: 0L
            delay(500L)
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val circleColor = MaterialTheme.colorScheme.primary
        Canvas(modifier = Modifier.size(200.dp)) {
            drawCircle(color = circleColor)
        }
        Slider(
            value = if (duration > 0) position / duration.toFloat() else 0f,
            onValueChange = { frac -> player.seekTo((duration * frac).toLong()) },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            IconButton(onClick = { viewModel.setEvent(MeditationContract.Event.PauseMeditation) }) {
                Icon(Icons.Filled.Pause, contentDescription = stringResource(id = com.example.spybrain.R.string.meditation_pause))
            }
            IconButton(onClick = { uiState.currentPlaying?.let { viewModel.setEvent(MeditationContract.Event.PlayMeditation(it)) } }) {
                Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(id = com.example.spybrain.R.string.meditation_play))
            }
            IconButton(onClick = {
                viewModel.setEvent(MeditationContract.Event.StopMeditation)
            }) {
                Icon(Icons.Filled.Stop, contentDescription = stringResource(id = com.example.spybrain.R.string.meditation_stop))
            }
        }
    }
} 