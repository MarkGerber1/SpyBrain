package com.example.spybrain.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import android.widget.Toast
import com.example.spybrain.presentation.settings.SettingsContract
import com.example.spybrain.presentation.settings.SettingsViewModel
import com.example.spybrain.presentation.settings.SettingsContract.Event
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.animation.Crossfade
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.CircleShape
import com.example.spybrain.R
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import android.app.Activity
import android.content.res.Configuration
import java.util.Locale
import android.speech.tts.Voice
import android.os.Vibrator
import android.content.Context
import androidx.compose.material.icons.filled.Vibration
import com.example.spybrain.service.VoiceAssistantService
import androidx.compose.ui.unit.Dp
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.res.stringResource

object LocaleManager {
    fun setLocale(activity: Activity, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(activity.resources.configuration)
        config.setLocale(locale)
        activity.createConfigurationContext(config)
    }
}

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
                    // Обновление UI при смене языка
                    // В реальном приложении здесь может быть дополнительная логика
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
                val currentLabel = state.availableTracks.firstOrNull { it.first == state.ambientTrack }?.second ?: stringResource(R.string.settings_select_track)
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
                Text(text = if (state.heartbeatEnabled) stringResource(R.string.settings_heartbeat_enabled) else stringResource(R.string.settings_heartbeat_disabled))
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
                Text(text = if (state.vibrationEnabled) stringResource(R.string.settings_vibration_enabled) else stringResource(R.string.settings_vibration_disabled))
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
                Text(text = if (state.voiceHintsEnabled) stringResource(R.string.settings_voice_hints_enabled) else stringResource(R.string.settings_voice_hints_disabled))
            }
        }
        
        item {
            Text(text = stringResource(R.string.settings_voice_tts), style = MaterialTheme.typography.titleLarge)
            VoiceSelection(state, viewModel)
        }
        
        item {
            Text(text = stringResource(R.string.settings_language), style = MaterialTheme.typography.titleLarge)
            val activity = context as? Activity
            
            val languageChangedText = { label: String -> context.getString(R.string.settings_language_changed, label) }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val languages = listOf("ru" to "Русский", "en" to "English")
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
                                    // Обновляем UI без перезапуска
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
                    contentDescription = "Выбрано",
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
        // Создаем простую версию без settingsDataStore для UI
        VoiceAssistantService(context, null)
    }
    var voices by remember { mutableStateOf<List<Voice>>(emptyList()) }
    LaunchedEffect(Unit) {
        voices = voiceService.getAvailableVoices()
    }
    Column {
        voices.forEach { voice ->
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