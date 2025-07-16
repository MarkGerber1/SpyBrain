package com.example.spybrain.presentation.stats

// TODO: Сбор всех TODO/FIXME по файлу ниже

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spybrain.R
import com.example.spybrain.domain.model.BreathingSession
import com.example.spybrain.domain.model.Session
import com.example.spybrain.util.VibrationUtil
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment

/**
 * Р­РЅР°СЂ СЃС‚Р°С‚РёСЃС‚РёРєРё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 * @param viewModel ViewModel СЃС‚Р°С‚РёСЃС‚РёРєРё.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // РЅР°РЅРёРјР°С†РёСЏ РґР»СЏ С„РѕРЅР°
    val infiniteTransition = rememberInfiniteTransition()
    val backgroundAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E).copy(alpha = backgroundAlpha),
                        Color(0xFF0D47A1),
                        Color(0xFF01579B)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Р·РіРѕР»РѕРІРѕРє СЃ Р°РЅРёРјР°С†РёРµР№
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(),
                    modifier = Modifier.animateContentSize()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "РЎС‚Р°С‚РёСЃС‚РёРєР° С‚СЂРµРЅРёСЂРѕРІРѕРє",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "РћС‚СЃР»РµР¶РёРІР°Р№С‚Рµ СЃРІРѕР№ РїСЂРѕРіСЂРµСЃСЃ",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // РѕР±С‰Р°СЏ СЃС‚Р°С‚РёСЃС‚РёРєР°
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    modifier = Modifier.animateContentSize()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "РѕР±С‰Р°СЏ СЃС‚Р°С‚РёСЃС‚РёРєР°",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            statRow(
                                label = "РјРµРґРёС‚Р°С†РёСЏ (РјРёРЅСѓС‚С‹)",
                                value = "${(state.stats?.totalMeditationTimeSeconds ?: 0) / 60}",
                                icon = Icons.Default.SelfImprovement,
                                color = Color(0xFF4CAF50)
                            )

                            statRow(
                                label = "Р”С‹С…Р°РЅРёРµ (РјРёРЅСѓС‚С‹)",
                                value = "${(state.stats?.totalBreathingTimeSeconds ?: 0) / 60}",
                                icon = Icons.Default.Air,
                                color = Color(0xFF2196F3)
                            )

                            statRow(
                                label = "РЎРµСЃСЃРёРё РјРµРґРёС‚Р°С†РёРё",
                                value = "${state.stats?.completedMeditationSessions ?: 0}",
                                icon = Icons.Default.Spa,
                                color = Color(0xFF9C27B0)
                            )

                            statRow(
                                label = "РЎРµСЃСЃРёРё РґС‹С…Р°РЅРёСЏ",
                                value = "${state.stats?.completedBreathingSessions ?: 0}",
                                icon = Icons.Default.Timeline,
                                color = Color(0xFFFF9800)
                            )

                            statRow(
                                label = "РўРµРєСѓС‰Р°СЏ СЃРµСЂРёСЏ",
                                value = "${state.stats?.currentStreakDays ?: 0}",
                                icon = Icons.Default.DateRange,
                                color = Color(0xFFE91E63)
                            )

                            statRow(
                                label = "Р›СѓС‡С€Р°СЏ СЃРµСЂРёСЏ",
                                value = "${state.stats?.longestStreakDays ?: 0}",
                                icon = Icons.Default.Star,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                }
            }

            // РёСЃС‚РѕСЂРёСЏ РґС‹С…Р°С‚РµР»СЊРЅС‹С… СЃРµСЃСЃРёР№
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    modifier = Modifier.animateContentSize()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "РёСЃС‚РѕСЂРёСЏ РґС‹С…Р°С‚РµР»СЊРЅС‹С… СЃРµСЃСЃРёР№",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            if (state.breathingHistory.isEmpty()) {
                                emptyStateMessage(
                                    message = "РќРµС‚ РґС‹С…Р°С‚РµР»СЊРЅС‹С… СЃРµСЃСЃРёР№",
                                    icon = Icons.Default.Air
                                )
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(state.breathingHistory.take(5)) { session ->
                                        sessionItem(
                                            session = session,
                                            onClick = {
                                                VibrationUtil.vibrateLight(context)
                                                // TODO: РџРѕРєР°Р·Р°С‚СЊ РґРµС‚Р°Р»Рё СЃРµСЃСЃРёРё
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // РёСЃС‚РѕСЂРёСЏ РјРµРґРёС‚Р°С†РёР№
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    modifier = Modifier.animateContentSize()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "РёСЃС‚РѕСЂРёСЏ РјРµРґРёС‚Р°С†РёР№",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            if (state.sessionHistory.isEmpty()) {
                                emptyStateMessage(
                                    message = "РќРµС‚ РјРµРґРёС‚Р°С†РёРѕРЅРЅС‹С… СЃРµСЃСЃРёР№",
                                    icon = Icons.Default.SelfImprovement
                                )
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(state.sessionHistory.take(5)) { session ->
                                        sessionItem(
                                            session = session,
                                            onClick = {
                                                VibrationUtil.vibrateLight(context)
                                                // TODO: РџРѕРєР°Р·Р°С‚СЊ РґРµС‚Р°Р»Рё СЃРµСЃСЃРёРё
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * РЅС‚СЂРѕРєР° СЃС‚Р°С‚РёСЃС‚РёРєРё.
 * @param label РќР°Р·РІР°РЅРёРµ РјРµС‚СЂРёРєРё.
 * @param value Р—РЅР°С‡РµРЅРёРµ РјРµС‚СЂРёРєРё.
 * @param icon РРєРѕРЅРєР°.
 * @param color Р¦РІРµС‚.
 * @param modifier РњРѕРґРёС„РёРєР°С‚РѕСЂ Compose.
 */
@Composable
fun statRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* TODO: РџРѕРєР°Р·Р°С‚СЊ РґРµС‚Р°Р»Рё */ },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun sessionItem(
    session: Session,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // РРєРѕРЅРєР°
            Icon(
                imageVector = Icons.Default.SelfImprovement,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // РРЅС„РѕСЂРјР°С†РёСЏ
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "РЎРµСЃСЃРёСЏ ${session.id.takeLast(8)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${session.durationSeconds / 60} РјРёРЅСѓС‚",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(session.startTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Р‘РµР№РґР¶ СЃ С‚РёРїРѕРј
            Badge(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    when (session.type) {
                        com.example.spybrain.domain.model.SessionType.MEDITATION -> "РјРµРґРёС‚Р°С†РёСЏ"
                        com.example.spybrain.domain.model.SessionType.BREATHING -> "Р”С‹С…Р°РЅРёРµ"
                        else -> "РЎРµСЃСЃРёСЏ"
                    }
                )
            }
        }
    }
}

@Composable
fun sessionItem(
    session: com.example.spybrain.domain.model.BreathingSession,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // РРєРѕРЅРєР°
            Icon(
                imageVector = Icons.Default.Air,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // РРЅС„РѕСЂРјР°С†РёСЏ
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.patternName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${session.durationSeconds / 60} РјРёРЅСѓС‚",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(session.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Р‘РµР№РґР¶ СЃ С‚РёРїРѕРј
            Badge(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("Р”С‹С…Р°РЅРёРµ")
            }
        }
    }
}

/**
 * РЎРѕРѕР±С‰РµРЅРёРµ Рѕ РїСѓСЃС‚РѕРј СЃРѕСЃС‚РѕСЏРЅРёРё РґР»СЏ РёСЃС‚РѕСЂРёРё.
 * @param message РўРµРєСЃС‚ СЃРѕРѕР±С‰РµРЅРёСЏ.
 * @param icon РРєРѕРЅРєР°.
 * @param modifier РњРѕРґРёС„РёРєР°С‚РѕСЂ Compose.
 */
@Composable
fun emptyStateMessage(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
