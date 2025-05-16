package com.example.spybrain.presentation.stats

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spybrain.domain.model.BreathingSession
import com.example.spybrain.domain.model.Session
import com.example.spybrain.presentation.base.UiEffect
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable // FIXME билд-фикс 09.05.2025
import com.example.spybrain.presentation.stats.StatsViewModel
import com.example.spybrain.presentation.stats.StatsContract
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.unit.sp
import com.example.spybrain.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val breathing by viewModel.breathingHistory.collectAsState()
    val meditations by viewModel.meditationHistory.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val timeFormatter = remember { java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") }
    val dateFormatter = remember { java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault()) }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { eff ->
            if (eff is StatsContract.Effect.ShowError) {
                Toast.makeText(context, eff.error.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.stats_title)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Сводка статистики
            item {
                StatsSummaryCard(state.stats)
            }
    
            // Заголовок для сеансов дыхания
            item {
                SectionHeader(
                    title = stringResource(id = R.string.stats_breathing_history_title),
                    icon = Icons.Default.Air
                )
            }
    
            // История сеансов дыхания
            if (breathing.isEmpty()) {
                item {
                    EmptyStateMessage(message = "У вас пока нет сеансов дыхания")
                }
            } else {
                items(breathing) { session ->
                    BreathingSessionCard(session, formatter = timeFormatter)
                }
            }
    
            // Заголовок для сеансов медитации
            item {
                SectionHeader(
                    title = stringResource(id = R.string.stats_meditation_history_title),
                    icon = Icons.Default.SelfImprovement
                )
            }
    
            // История медитаций
            if (meditations.isEmpty()) {
                item {
                    EmptyStateMessage(message = "У вас пока нет сеансов медитации")
                }
            } else {
                items(meditations) { session ->
                    MeditationSessionCard(session, formatter = dateFormatter)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatsSummaryCard(stats: com.example.spybrain.domain.model.Stats?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Статистика тренировок",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (stats != null) {
                // Медитация и дыхание
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard(
                        title = "Медитация",
                        value = "${stats.totalMeditationTimeSeconds / 60}",
                        unit = "минут",
                        icon = Icons.Default.SelfImprovement,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    StatCard(
                        title = "Дыхание",
                        value = "${stats.totalBreathingTimeSeconds / 60}",
                        unit = "минут",
                        icon = Icons.Default.Air,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Количество сессий
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard(
                        title = "Сессии медитации",
                        value = "${stats.completedMeditationSessions}",
                        unit = "сессий",
                        icon = Icons.Default.Spa,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    StatCard(
                        title = "Сессии дыхания",
                        value = "${stats.completedBreathingSessions}",
                        unit = "сессий",
                        icon = Icons.Default.Timeline,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Серии
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard(
                        title = "Текущая серия",
                        value = "${stats.currentStreakDays}",
                        unit = "дней",
                        icon = Icons.Default.DateRange,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    StatCard(
                        title = "Лучшая серия",
                        value = "${stats.longestStreakDays}",
                        unit = "дней",
                        icon = Icons.Default.DateRange,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Text(
                    text = "Нет доступной статистики",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun BreathingSessionCard(session: BreathingSession, formatter: DateTimeFormatter) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка
            Icon(
                imageVector = Icons.Default.Air,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
            )
            
            // Информация
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.patternName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(
                        id = com.example.spybrain.R.string.duration_seconds_format,
                        session.durationSeconds
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = session.timestamp.format(formatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Статус завершения
            if (session.completed) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text("Завершено")
                }
            }
        }
    }
}

@Composable
fun MeditationSessionCard(session: Session, formatter: SimpleDateFormat) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка
            Icon(
                imageVector = Icons.Default.SelfImprovement,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
            )
            
            // Информация
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.id.substringAfterLast("_").let {
                        if (it.length > 10) "Медитация ${it.take(8)}..." else "Медитация $it"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(
                        id = com.example.spybrain.R.string.duration_seconds_format,
                        session.durationSeconds
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatter.format(session.startTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Длительность в минутах в виде бейджа
            val minutes = session.durationSeconds / 60
            Badge(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ) {
                Text("$minutes мин")
            }
        }
    }
} 