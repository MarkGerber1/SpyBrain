package com.example.spybrain.presentation.settings

// TODO: Сбор всех TODO/FIXME по файлу ниже

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spybrain.R
import com.example.spybrain.presentation.settings.SettingsContract
import com.example.spybrain.presentation.settings.SettingsViewModel
import com.example.spybrain.presentation.settings.SettingsContract.Event
import com.example.spybrain.service.VoiceAssistantService
import java.util.Locale
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import android.speech.tts.Voice

// Moved to dedicated file LocaleManager.kt

/**
 */
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsContract.Effect.NavigateTo -> navController.navigate(effect.screen.route)
                is SettingsContract.Effect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is SettingsContract.Effect.RefreshUI -> {
                    // РћР±РЅРѕРІР»РµРЅРёРµ UI РїСЂРё СЃРјРµРЅРµ СЏР·С‹РєР°
                    // Р’ СЂРµР°Р»СЊРЅРѕРј РїСЂРёР»РѕР¶РµРЅРёРё Р·РґРµСЃСЊ РјРѕР¶РµС‚ Р±С‹С‚СЊ РґРѕРїРѕР»РЅРёС‚РµР»СЊРЅР°СЏ Р»РѕРіРёРєР°
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(text = stringResource(R.string.settings_current_theme), style = MaterialTheme.typography.titleLarge)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                val themePreview = state.theme
                val bgRes = when (themePreview) {
                    "water" -> R.drawable.bg_water
                    "space" -> R.drawable.bg_space
                    "nature" -> R.drawable.bg_nature
                    "air" -> R.drawable.bg_air
                    else -> R.drawable.bg_nature
                }
                Image(
                    painter = painterResource(id = bgRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                ThemeIcon(
                    theme = themePreview,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp),
                    selected = true
                )
            }
        }

        item {
            Text(text = stringResource(R.string.settings_select_theme), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            val themes = listOf(
                Triple("water", R.string.settings_water, R.drawable.bg_water),
                Triple("space", R.string.settings_space, R.drawable.bg_space),
                Triple("nature", R.string.settings_nature, R.drawable.bg_nature),
                Triple("air", R.string.settings_air, R.drawable.bg_air)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(240.dp)
            ) {
                items(themes) { (themeId, themeNameRes, bgRes) ->
                    ThemePreviewCard(
                        themeId = themeId,
                        themeName = stringResource(themeNameRes),
                        bgRes = bgRes,
                        isSelected = state.theme == themeId,
                        onClick = { viewModel.setEvent(Event.ThemeSelected(themeId)) }
                    )
                }
            }
        }

        item {
            Text(text = stringResource(R.string.settings_ambient_music), style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = state.ambientEnabled,
                    onCheckedChange = { viewModel.setEvent(Event.AmbientToggled(it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (state.ambientEnabled) stringResource(R.string.settings_ambient_enabled) else stringResource(R.string.settings_ambient_disabled))
            }
            if (state.ambientEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                var expanded by remember { mutableStateOf(false) }
                val currentLabel = state.availableTracks.firstOrNull {
                    it.first == state.ambientTrack
                }?.second ?: stringResource(R.string.settings_select_track)
                Box {
                    Text(
                        text = currentLabel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
                            .padding(8.dp)
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        state.availableTracks.forEach { (id, title) ->
                            DropdownMenuItem(
                                text = { Text(title) },
                                onClick = {
                                    viewModel.setEvent(Event.AmbientTrackSelected(id))
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.settings_ambient_volume))
                androidx.compose.material3.Slider(
                    value = state.ambientVolume,
                    onValueChange = { viewModel.setEvent(Event.AmbientVolumeChanged(it)) },
                    valueRange = 0f..1f
                )
            }
        }

        item {
            Text(text = stringResource(R.string.settings_heartbeat), style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = state.heartbeatEnabled,
                    onCheckedChange = { viewModel.setEvent(Event.HeartbeatToggled(it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.heartbeatEnabled)
                        stringResource(R.string.settings_heartbeat_enabled)
                    else
                        stringResource(R.string.settings_heartbeat_disabled)
                )
            }
        }

        item {
            Text(text = stringResource(R.string.settings_vibration), style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Vibration,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = state.vibrationEnabled,
                    onCheckedChange = { viewModel.setEvent(Event.VibrationToggled(it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.vibrationEnabled)
                        stringResource(R.string.settings_vibration_enabled)
                    else
                        stringResource(R.string.settings_vibration_disabled)
                )
            }
        }

        item {
            Text(text = stringResource(R.string.settings_video_backgrounds), style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star, // Используем Star как иконку видео
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = state.videoBackgroundsEnabled,
                    onCheckedChange = { viewModel.setEvent(Event.VideoBackgroundsToggled(it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.videoBackgroundsEnabled)
                        stringResource(R.string.settings_video_backgrounds_enabled)
                    else
                        stringResource(R.string.settings_video_backgrounds_disabled)
                )
            }
        }

        item {
            Text(text = "Живые фоны", style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star, // Используем Star как иконку живых фонов
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = state.animatedBackgroundsEnabled,
                    onCheckedChange = { viewModel.setEvent(Event.AnimatedBackgroundsToggled(it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.animatedBackgroundsEnabled)
                        "Включены"
                    else
                        "Выключены"
                )
            }
        }

        item {
            Text(text = stringResource(R.string.settings_voice_hints), style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = state.voiceHintsEnabled,
                    onCheckedChange = { viewModel.setEvent(Event.VoiceHintsToggled(it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.voiceHintsEnabled)
                        stringResource(R.string.settings_voice_hints_enabled)
                    else
                        stringResource(R.string.settings_voice_hints_disabled)
                )
            }
        }

        item {
            Text(text = stringResource(R.string.settings_voice_tts), style = MaterialTheme.typography.titleLarge)
            var open by remember { mutableStateOf(false) }
            Button(onClick = { open = true }) { Text(stringResource(id = R.string.settings_voice_tts)) }
            if (open) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { open = false },
                    confirmButton = {
                        Button(onClick = { open = false }) { Text(stringResource(id = R.string.common_ok)) }
                    },
                    title = { Text(stringResource(id = R.string.settings_voice_tts)) },
                    text = {
                        val ctx = LocalContext.current
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            VoiceSelection(state, viewModel)
                            val style = when {
                                state.voiceRate <= 0.8f && state.voicePitch <= 0.95f -> "Мягкий"
                                state.voiceRate >= 1.1f || state.voicePitch >= 1.1f -> "Энергичный"
                                else -> "Нейтральный"
                            }
                            Text(text = "Стиль голоса: $style")
                            Spacer(Modifier.height(8.dp))
                            Text("Скорость речи")
                            androidx.compose.material3.Slider(
                                value = state.voiceRate,
                                onValueChange = { viewModel.setEvent(SettingsContract.Event.VoiceRateChanged(it)) },
                                valueRange = 0.5f..1.5f
                            )
                            Text("Тональность")
                            androidx.compose.material3.Slider(
                                value = state.voicePitch,
                                onValueChange = { viewModel.setEvent(SettingsContract.Event.VoicePitchChanged(it)) },
                                valueRange = 0.75f..1.5f
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = {
                                try {
                                    val intent = android.content.Intent("com.android.settings.TTS_SETTINGS").apply { addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK) }
                                    ctx.startActivity(intent)
                                } catch (_: Exception) {
                                    try {
                                        val fallback = android.content.Intent(android.provider.Settings.ACTION_SETTINGS).apply { addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK) }
                                        ctx.startActivity(fallback)
                                    } catch (_: Exception) { }
                                }
                            }) { Text("Открыть настройки TTS") }
                        }
                    }
                )
            }
        }

        item {
            Text(text = stringResource(R.string.settings_language), style = MaterialTheme.typography.titleLarge)
            val activity = context as? Activity

            val languageChangedText = { label: String -> context.getString(R.string.settings_language_changed, label) }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val languages = listOf("ru" to context.getString(R.string.settings_language_ru), "en" to context.getString(R.string.settings_language_en))
                val currentLanguage = Locale.getDefault().language

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    languages.forEach { (lang, label) ->
                        Button(
                            onClick = {
                                if (activity != null) {
                                    Toast.makeText(
                                        context,
                                        languageChangedText(label),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    LocaleManager.setLocale(activity, lang)
                                    // РћР±РЅРѕРІР»СЏРµРј UI Р±РµР· РїРµСЂРµР·Р°РїСѓСЃРєР°
                                    viewModel.setEvent(Event.LanguageChanged(lang))
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentLanguage == lang)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface,
                                contentColor = if (currentLanguage == lang)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(label)
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.settings_language_restart_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Text(text = "Живой фон", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            val styles = listOf(
                "auto" to "Авто",
                "ocean" to "Океан",
                "waterfall" to "Водопад",
                "clouds" to "Облака",
                "stars" to "Звёзды",
                "forest" to "Лес",
                "mountains" to "Горы",
                "rain" to "Дождь",
                "sky" to "Небо"
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                styles.forEach { (key, label) ->
                    androidx.compose.material3.FilterChip(
                        selected = state.backgroundStyle == key,
                        onClick = { viewModel.setEvent(Event.BackgroundStyleChanged(key)) },
                        label = { Text(label) }
                    )
                }
            }
        }

        item {
            Text(text = "Профиль", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.OutlinedTextField(
                value = state.userName,
                onValueChange = { viewModel.setEvent(Event.UserNameChanged(it)) },
                label = { Text("Имя") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.OutlinedTextField(
                value = if (state.userAge == 0) "" else state.userAge.toString(),
                onValueChange = { value ->
                    val digits = value.filter { it.isDigit() }
                    digits.toIntOrNull()?.let { viewModel.setEvent(Event.UserAgeChanged(it)) }
                },
                label = { Text("Возраст") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val genders = listOf("male" to "Мужской", "female" to "Женский", "other" to "Другое")
                genders.forEach { (key, label) ->
                    androidx.compose.material3.FilterChip(
                        selected = state.userGender == key,
                        onClick = { viewModel.setEvent(Event.UserGenderChanged(key)) },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate(com.example.spybrain.presentation.navigation.Screen.Main.route) }) {
                Text("На главный экран")
            }
        }
    }
}

@Composable
fun ThemePreviewCard(
    themeId: String,
    themeName: String,
    bgRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = bgRes),
                contentDescription = "$themeName ($themeId)",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            Text(
                text = themeName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            )

            if (isSelected) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "Р С‹Р±СЂРЅРѕ",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ThemeIcon(
    theme: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    size: Dp = 40.dp
) {
    val iconRes = when (theme) {
        "water" -> R.drawable.ic_water
        "space" -> R.drawable.ic_space
        "nature" -> R.drawable.ic_nature
        "air" -> R.drawable.ic_air
        else -> R.drawable.ic_nature
    }
    val bgColor = when (theme) {
        "water" -> Color(0xFFB3E5FC)
        "space" -> Color(0xFFB39DDB)
        "nature" -> Color(0xFFC8E6C9)
        "air" -> Color(0xFFE1F5FE)
        else -> Color(0xFFE0E0E0)
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .shadow(if (selected) 6.dp else 0.dp, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = theme,
            modifier = Modifier.size(size * 0.6f),
            tint = Color.Unspecified
        )
    }
}

@Composable
fun VoiceSelection(
    state: SettingsContract.State,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val voiceService = remember {
        // Use DI-provided VoiceAssistantService via AppEntryPoints
        dagger.hilt.android.EntryPointAccessors.fromApplication(
            context.applicationContext,
            com.example.spybrain.di.AppEntryPoints::class.java
        ).voiceAssistantService()
    }
    var voices by remember { mutableStateOf<List<Voice>>(emptyList()) }
    LaunchedEffect(Unit) {
        // Пробуем несколько раз, пока TTS инициализируется и отдаёт список
        repeat(5) {
            val list = voiceService.getAvailableVoices()
            if (list.isNotEmpty()) {
                val sorted = list.sortedWith(compareByDescending<Voice> {
                    val feat = it.features ?: emptySet()
                    feat.any { f -> f.contains("female", true) } || it.name.contains("female", true)
                }.thenByDescending { it.quality })
                voices = sorted
                return@LaunchedEffect
            }
            kotlinx.coroutines.delay(400)
        }
        val fallback = voiceService.getAvailableVoices()
        voices = fallback.sortedByDescending { it.quality }
    }
    Column {
        if (voices.isEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Загрузка голосов…")
            }
        }
        voices.forEach { voice: Voice ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.voiceId == voice.name,
                    onClick = {
                        viewModel.setEvent(SettingsContract.Event.VoiceIdSelected(voice.name))
                        voiceService.setVoiceById(voice.name)
                    }
                )
                Text(voiceService.getVoiceDescription(voice))
            }
        }
    }
}
