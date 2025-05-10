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
import com.example.spybrain.voice.VoiceAssistantService
import androidx.compose.ui.unit.Dp
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

object LocaleManager {
    fun setLocale(activity: Activity, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(activity.resources.configuration)
        config.setLocale(locale)
        activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
    }
}

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsContract.Effect.NavigateTo -> navController.navigate(effect.screen.route)
                is SettingsContract.Effect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
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
            Text(text = "Текущая тема", style = MaterialTheme.typography.titleLarge)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Crossfade(targetState = state.theme) { themePreview ->
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
        }
        item {
            Text(text = "Тема", style = MaterialTheme.typography.titleLarge)
            val themes = listOf("water" to "Вода", "space" to "Космос", "nature" to "Природа", "air" to "Воздух")
            val themeLabels = themes.map { it.second }
            com.example.spybrain.presentation.components.IconMenuGrid(
                icons = List(themes.size) { Icons.Default.Star },
                labels = themeLabels,
                onClick = { idx -> viewModel.setEvent(Event.ThemeSelected(themes[idx].first)) }
            )
        }
        item {
            Text(text = "Фоновая музыка", style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = state.ambientEnabled,
                    onCheckedChange = { viewModel.setEvent(Event.AmbientToggled(it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (state.ambientEnabled) "Включена" else "Выключена")
            }
            if (state.ambientEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                var expanded by remember { mutableStateOf(false) }
                val currentLabel = state.availableTracks.firstOrNull { it.first == state.ambientTrack }?.second ?: "Выберите трек"
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
            Text(text = "Бьющееся сердце", style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = state.heartbeatEnabled,
                    onCheckedChange = { viewModel.setEvent(Event.HeartbeatToggled(it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (state.heartbeatEnabled) "Включено" else "Выключено")
            }
        }
        item {
            Text(text = "Голосовые подсказки (TTS)", style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = state.voiceHintsEnabled,
                    onCheckedChange = { viewModel.setEvent(Event.VoiceHintsToggled(it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (state.voiceHintsEnabled) "Включено" else "Выключено")
            }
        }
        item {
            Text(text = "Голос TTS", style = MaterialTheme.typography.titleLarge)
            val voiceService = remember { VoiceAssistantService(context) }
            val voices = remember { voiceService.getAvailableVoices() }
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
        item {
            Text(text = "Язык", style = MaterialTheme.typography.titleLarge)
            val activity = context as? Activity
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val languages = listOf("ru" to "Русский", "en" to "English")
                languages.forEach { (lang, label) ->
                    OutlinedButton(
                        onClick = {
                            if (activity != null) {
                                LocaleManager.setLocale(activity, lang)
                                activity.recreate()
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (Locale.getDefault().language == lang) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent
                        )
                    ) {
                        Text(label)
                    }
                }
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