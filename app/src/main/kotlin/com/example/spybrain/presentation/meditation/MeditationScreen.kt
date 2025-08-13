package com.example.spybrain.presentation.meditation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.example.spybrain.presentation.components.MediaControls
import androidx.activity.compose.BackHandler
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
import com.example.spybrain.service.AmbientMusicService
import com.example.spybrain.service.VoiceAssistantService
import com.example.spybrain.data.repository.MeditationRepositoryImpl.MeditationTrack
import com.example.spybrain.presentation.meditation.MeditationViewModel
import com.example.spybrain.presentation.meditation.MeditationContract
import com.example.spybrain.presentation.theme.DynamicBackground
import kotlinx.coroutines.delay
import android.widget.Toast
import android.content.Intent
import timber.log.Timber
import com.example.spybrain.meditationInfoTabs

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
        // РЎРѕР·РґР°РµРј РїСЂРѕСЃС‚СѓСЋ РІРµСЂСЃРёСЋ Р±РµР· settingsDataStore РґР»СЏ UI
        VoiceAssistantService(context, null)
    }
    val player = viewModel.player

    // DisposableEffect РґР»СЏ РѕС‡РёСЃС‚РєРё СЂРµСЃСѓСЂСЃРѕРІ РїСЂРё СЂР°Р·РјРѕРЅС‚РёСЂРѕРІР°РЅРёРё РєРѕРјРїРѕР·Р°Р±Р»Р°
    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanupResources()
        }
    }

    // Ambient background audio: no auto-start here; only ensure stop if disabled handled in MainScreen

    // РћР±СЂР°Р±РѕС‚РєР° СЌС„С„РµРєС‚РѕРІ РѕС‚ ViewModel, РІРєР»СЋС‡Р°СЏ РіРѕР»РѕСЃРѕРІС‹Рµ РїРѕРґСЃРєР°Р·РєРё
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
                            Toast.makeText(context, R.string.voice_hint_error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is MeditationContract.Effect.TrackStarted -> {
                    // РњРѕР¶РЅРѕ РґРѕР±Р°РІРёС‚СЊ СѓРІРµРґРѕРјР»РµРЅРёРµ Рѕ РЅР°С‡Р°Р»Рµ С‚СЂРµРєР°
                    Timber.d("Track started: ${effect.track.id}")
                }
                is MeditationContract.Effect.TrackCompleted -> {
                    // РњРѕР¶РЅРѕ РґРѕР±Р°РІРёС‚СЊ СѓРІРµРґРѕРјР»РµРЅРёРµ Рѕ Р·Р°РІРµСЂС€РµРЅРёРё С‚СЂРµРєР°
                    Timber.d("Track completed: ${effect.track.id}")
                }
            }
        }
    }

    DynamicBackground(lottieKeyOverride = if (state.isGuidedMode) "lottie_guided" else "lottie_meditation") {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.meditation_title)) }
            )
        }
    ) { paddingValues ->
        // Р—Р°РјРµРЅСЏРµРј Crossfade РЅР° РїСЂРѕСЃС‚РѕР№ Box СЃ key РґР»СЏ РїРµСЂРµСЃРѕР·РґР°РЅРёСЏ РїСЂРё СЃРјРµРЅРµ С‚РµРјС‹
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            val theme = settings.theme

            // РћРїСЂРµРґРµР»СЏРµРј СЂРµСЃСѓСЂСЃС‹ С„РѕРЅР° Рё РёРєРѕРЅРєРё Р±РµР· РёСЃРїРѕР»СЊР·РѕРІР°РЅРёСЏ remember
            val themePack = com.example.spybrain.presentation.theme.ThemePacks.themePackFor(theme)
            val iconPack = com.example.spybrain.presentation.theme.ThemePacks.iconPackFor(theme)
            val bgPainter = painterResource(id = themePack.backgroundImageRes)
            val themeIconRes = iconPack.themeIconRes

            Image(
                painter = bgPainter,
                contentDescription = stringResource(id = R.string.meditation_background_image_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            // РџРѕР»СѓРїСЂРѕР·СЂР°С‡РЅС‹Р№ РѕРІРµСЂР»РµР№ РґР»СЏ Р»СѓС‡С€РµР№ С‡РёС‚Р°РµРјРѕСЃС‚Рё
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            // РРєРѕРЅРєР° С‚РµРјС‹
            Icon(
                painter = painterResource(id = themeIconRes),
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
                    // РџРѕРєР°Р·С‹РІР°РµРј РІС‹Р±РѕСЂ РјРµР¶РґСѓ РјРµРґРёС‚Р°С†РёСЏРјРё Рё С‚СЂРµРєР°РјРё
                    var selectedMode by remember { mutableStateOf("meditations") }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // РџРµСЂРµРєР»СЋС‡Р°С‚РµР»СЊ СЂРµР¶РёРјРѕРІ
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // РљРЅРѕРїРєР° "РњРµРґРёС‚Р°С†РёРё"
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

                            // Кнопка "Медитации с инструкцией"
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
                                    text = stringResource(id = R.string.meditation_guided_tab_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        // Voice intro when entering basic meditation tab; stop guidance when switching back
                        LaunchedEffect(selectedMode) {
                            if (settings.voiceHintsEnabled) {
                                if (selectedMode == "meditations") {
                                    try { voiceService.speakIntro() } catch (_: Exception) {}
                                    viewModel.setEvent(MeditationContract.Event.SetGuidedMode(false))
                                } else {
                                    try { voiceService.stopGuidance() } catch (_: Exception) {}
                                    viewModel.setEvent(MeditationContract.Event.SetGuidedMode(true))
                                }
                            }
                        }

                        // РљРѕРЅС‚РµРЅС‚ РІ Р·Р°РІРёСЃРёРјРѕСЃС‚Рё РѕС‚ РІС‹Р±СЂР°РЅРЅРѕРіРѕ СЂРµР¶РёРјР°
                        when (selectedMode) {
                            "meditations" -> MeditationList(viewModel, settings.voiceHintsEnabled)
                            "tracks" -> GuidedMeditationsTab(
                                viewModel = viewModel,
                                voiceHintsEnabled = settings.voiceHintsEnabled,
                                onStartGuidance = { intervalSec ->
                                    if (settings.voiceHintsEnabled) {
                                        try { voiceService.startGuidance(intervalSec) } catch (_: Exception) {}
                                    }
                                },
                                onStopGuidance = {
                                    try { voiceService.stopGuidance() } catch (_: Exception) {}
                                }
                            )
                        }
                    }
                }
                else -> MeditationPlayerUI(viewModel, state, player, settings.voiceHintsEnabled)
            }
        }
    }
    }
}

@Composable
fun GuidedMeditationItem(
    meditation: Meditation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showInfoSheet by remember { mutableStateOf(false) }
    var infoTabIndex by remember { mutableStateOf(0) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = meditation.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = meditation.description ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 2
                )
            }

            IconButton(onClick = { menuExpanded = true }) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Color.White)
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.meditation_about)) },
                    onClick = {
                        menuExpanded = false
                        infoTabIndex = 0
                        showInfoSheet = true
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.meditation_how_to)) },
                    onClick = {
                        menuExpanded = false
                        infoTabIndex = 1
                        showInfoSheet = true
                    }
                )
            }
        }
    }

    if (showInfoSheet) {
        val tabs = meditationInfoTabs(meditation)
        com.example.spybrain.presentation.components.InfoBottomSheet(
            title = meditation.title,
            tabs = tabs,
            onDismiss = { showInfoSheet = false }
        )
    }
}

@Composable
fun MeditationList(
    viewModel: MeditationViewModel,
    voiceHintsEnabled: Boolean
) {
    val context = LocalContext.current
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
                text = stringResource(id = R.string.meditation_select_title),
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
                                    viewModel.setEvent(MeditationContract.Event.VoiceCommand(
                                        context.getString(R.string.meditation_voice_start, meditation.title)
                                    ))
                                }
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(text = meditation.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = meditation.description ?: "",
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
                                    text = meditation.category ?: "",
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

    // РћР±РЅРѕРІР»СЏРµРј РїРѕР·РёС†РёСЋ РїСЂРѕРёРіСЂС‹РІР°РЅРёСЏ
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
        BackHandler(enabled = true) {
            viewModel.setEvent(MeditationContract.Event.BackPressed)
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Р—Р°РіРѕР»РѕРІРѕРє РјРµРґРёС‚Р°С†РёРё
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
                        text = meditation.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            // Р’РёР·СѓР°Р»РёР·Р°С†РёСЏ РјРµРґРёС‚Р°С†РёРё
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(2000)) + slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(2000)
                )
            ) {
                MeditationCircle(isPlaying = isPlaying)
            }

            // РџСЂРѕРіСЂРµСЃСЃ Рё РєРѕРЅС‚СЂРѕР»Р»РµСЂС‹
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
                    // Р’СЂРµРјСЏ
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

                    // РџСЂРѕРіСЂРµСЃСЃ-Р±Р°СЂ
                    Slider(
                        value = if (duration > 0) position / duration.toFloat() else 0f,
                        onValueChange = { frac -> player.seekTo((duration * frac).toLong()) },
                        modifier = Modifier.fillMaxWidth()
                    )

            MediaControls(
                            isPlaying = isPlaying,
                isPaused = !isPlaying,
                onPlay = {
                                    uiState.currentPlaying?.let {
                                        viewModel.setEvent(MeditationContract.Event.PlayMeditation(it))
                                    }
                },
                onPause = { viewModel.setEvent(MeditationContract.Event.PauseMeditation) },
                onStop = { viewModel.setEvent(MeditationContract.Event.StopMeditation) },
                onBack = { viewModel.setEvent(MeditationContract.Event.StopMeditation) },
                modifier = Modifier.padding(bottom = 24.dp)
            )
                }
            }
        }
    }
}

@Composable
fun MeditationCircle(isPlaying: Boolean) {
    // РђРЅРёРјР°С†РёСЏ РїСѓР»СЊСЃР°С†РёРё
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val animationValue by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        )
    )

    val size = if (isPlaying) animationValue else 0.7f
    val color = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier.size(250.dp),
        contentAlignment = Alignment.Center
    ) {
        // Р“СЂР°РґРёРµРЅС‚РЅС‹Р№ РєСЂСѓРі
        Canvas(modifier = Modifier.matchParentSize()) {
            val centerX = this.size.width / 2f
            val centerY = this.size.height / 2f
            val radius = (this.size.width.coerceAtMost(this.size.height) / 2f) * size

            // Р“СЂР°РґРёРµРЅС‚РЅРѕРµ Р·Р°РїРѕР»РЅРµРЅРёРµ
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

            // РљРѕРЅС‚СѓСЂ
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
            .clickable { /* Р”РѕРїРѕР»РЅРёС‚РµР»СЊРЅРѕРµ РґРµР№СЃС‚РІРёРµ */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_nature),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}

// Р¤РѕСЂРјР°С‚РёСЂРѕРІР°РЅРёРµ РІСЂРµРјРµРЅРё РІ С„РѕСЂРјР°С‚ MM:SS
private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun GuidedMeditationsTab(
    viewModel: MeditationViewModel,
    voiceHintsEnabled: Boolean,
    modifier: Modifier = Modifier,
    onStartGuidance: (Int) -> Unit = {},
    onStopGuidance: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Р—Р°РіРѕР»РѕРІРѕРє СЃРµРєС†РёРё
        Text(
            text = stringResource(id = R.string.guided_meditations_title),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Управляем голосовыми подсказками в этом табе
        LaunchedEffect(voiceHintsEnabled) {
            if (voiceHintsEnabled) onStartGuidance(40) else onStopGuidance()
        }

        // Список, группируемый по категории
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val groups = state.meditations.groupBy { it.category ?: context.getString(R.string.categories) }
            groups.forEach { (category, itemsInCategory) ->
                item {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                items(itemsInCategory) { meditation ->
                    GuidedMeditationItem(
                        meditation = meditation,
                        onClick = {
                            viewModel.setEvent(MeditationContract.Event.PlayMeditation(meditation))
                            if (voiceHintsEnabled) {
                                // Немедленно озвучиваем приветствие
                                // Делаем через эффект, т.к. тут нет доступа к voiceService
                                viewModel.setEvent(
                                    MeditationContract.Event.VoiceCommand(
                                        context.getString(R.string.meditation_voice_greeting, meditation.title)
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }

        // Р­Р»РµРјРµРЅС‚С‹ СѓРїСЂР°РІР»РµРЅРёСЏ РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёРµРј
        // Элементы управления плеером остаются в основном UI (при воспроизведении)
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
            // РРєРѕРЅРєР° С‚СЂРµРєР°
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
                    // РђРЅРёРјРёСЂРѕРІР°РЅРЅР°СЏ РёРєРѕРЅРєР° РІРѕСЃРїСЂРѕРёР·РІРµРґРµРЅРёСЏ
                    val infiniteTransition = rememberInfiniteTransition(label = "playing_animation")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 0.8f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        )
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

            // РРЅС„РѕСЂРјР°С†РёСЏ Рѕ С‚СЂРµРєРµ
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

            // РРЅРґРёРєР°С‚РѕСЂ РІС‹Р±РѕСЂР°
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
            // РќР°Р·РІР°РЅРёРµ С‚РµРєСѓС‰РµРіРѕ С‚СЂРµРєР°
            Text(
                text = stringResource(id = track.titleRes),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // РџСЂРѕРіСЂРµСЃСЃ-Р±Р°СЂ
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

            // РљРЅРѕРїРё СѓРїСЂР°РІР»РµРЅРёСЏ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // РљРЅРѕРїРєР° "РџСЂРµРґС‹РґСѓС‰РёР№"
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

                // РљРЅРѕРїРєР° Play/Pause
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

                // РљРЅРѕРїРєР° "РЎР»РµРґСѓСЋС‰РёР№"
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
