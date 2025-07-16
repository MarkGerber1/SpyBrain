package com.example.spybrain.presentation.achievements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spybrain.R
import com.example.spybrain.domain.model.Achievement
import com.example.spybrain.domain.model.AchievementType
import com.example.spybrain.presentation.theme.DynamicBackground
import java.time.format.DateTimeFormatter
import android.widget.Toast
import com.example.spybrain.util.UiError
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import com.example.spybrain.presentation.achievements.AchievementsContract
import com.example.spybrain.presentation.achievements.AchievementsViewModel

/**
 * @param viewModel ViewModel достижений.
 */
@Composable
fun achievementsScreen(
    viewModel: AchievementsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showAchievementDetails by remember { mutableStateOf<Achievement?>(null) }

    // Обрабатываем эффекты
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AchievementsContract.Effect.ShowAchievementDetails -> {
                    showAchievementDetails = effect.achievement
                }
                is AchievementsContract.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is AchievementsContract.Effect.ShowError -> {
                    val errorMessage = when (val err = effect.error) {
                        is UiError.Custom -> err.message
                        is UiError.NetworkError -> "Ошибка сети"
                        is UiError.ValidationError -> "Ошибка валидации"
                        is UiError.UnknownError -> "Неизвестная ошибка"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DynamicBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Заголовок
            Text(
                text = "Достижения",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Информация об уровне пользователя
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Круг с уровнем
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.userLevel.level.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Уровень ${state.userLevel.level}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Всего очков: ${state.totalPoints}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Индикатор прогресса
                    val animatedProgress by animateFloatAsState(
                        targetValue = state.userLevel.progress,
                        label = "Level Progress"
                    )

                    Column {
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${state.userLevel.currentPoints}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "${state.userLevel.requiredPoints}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Табы для выбора категории достижений
            ScrollableTabRow(
                selectedTabIndex = state.selectedTab.ordinal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                AchievementType.values().forEachIndexed { index, type ->
                    Tab(
                        selected = state.selectedTab.ordinal == index,
                        onClick = { viewModel.setEvent(AchievementsContract.Event.TabSelected(type)) },
                        text = {
                            Text(
                                text = when (type) {
                                    AchievementType.GENERAL -> "Общие"
                                    AchievementType.MEDITATION -> "Медитация"
                                    AchievementType.BREATHING -> "Дыхание"
                                    AchievementType.STREAK -> "Серия"
                                    AchievementType.SESSIONS -> "Сессии"
                                }
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = when (type) {
                                    AchievementType.GENERAL -> Icons.Default.Star
                                    AchievementType.MEDITATION -> Icons.Default.SelfImprovement
                                    AchievementType.BREATHING -> Icons.Default.Air
                                    AchievementType.STREAK -> Icons.Default.Timeline
                                    AchievementType.SESSIONS -> Icons.Default.Tag
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // Список достижений
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                val filteredAchievements = state.achievements.filter { it.type == state.selectedTab }

                if (filteredAchievements.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет достижений в этой категории",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredAchievements) { achievement ->
                            achievementItem(
                                achievement = achievement,
                                onClick = {
                                    viewModel.setEvent(AchievementsContract.Event.AchievementClicked(achievement))
                                }
                            )
                        }

                        // Добавляем отступ внизу
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }

        // Диалог с подробной информацией о достижении
        showAchievementDetails?.let { achievement ->
            AchievementDetailsDialog(
                achievement = achievement,
                onDismiss = { showAchievementDetails = null }
            )
        }
    }
}

/**
 * Карточка достижения.
 * @param achievement Данные достижения.
 * @param onClick Обработчик клика.
 */
@Composable
fun achievementItem(
    achievement: Achievement,
    onClick: () -> Unit
) {
    val backgroundColor = if (achievement.isUnlocked) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка достижения
            Icon(
                imageVector = when (achievement.type) {
                    AchievementType.GENERAL -> Icons.Default.Star
                    AchievementType.MEDITATION -> Icons.Default.SelfImprovement
                    AchievementType.BREATHING -> Icons.Default.Air
                    AchievementType.STREAK -> Icons.Default.Timeline
                    AchievementType.SESSIONS -> Icons.Default.Tag
                },
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(8.dp),
                tint = if (achievement.isUnlocked)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (achievement.isUnlocked)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Индикатор прогресса
                LinearProgressIndicator(
                    progress = { achievement.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (achievement.isUnlocked)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Очки
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.points.toString(),
                    color = if (achievement.isUnlocked)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Диалог с деталями достижения.
 * @param achievement Данные достижения.
 * @param onDismiss Обработчик закрытия.
 */
@Composable
fun AchievementDetailsDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Заголовок
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (achievement.type) {
                            AchievementType.GENERAL -> Icons.Default.Star
                            AchievementType.MEDITATION -> Icons.Default.SelfImprovement
                            AchievementType.BREATHING -> Icons.Default.Air
                            AchievementType.STREAK -> Icons.Default.Timeline
                            AchievementType.SESSIONS -> Icons.Default.Tag
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(4.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = achievement.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Описание
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Метрики
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Очки",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = achievement.points.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = "Прогресс",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "${(achievement.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = "Статус",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = if (achievement.isUnlocked) "Получено" else "В процессе",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (achievement.isUnlocked)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Прогресс
                LinearProgressIndicator(
                    progress = { achievement.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = achievement.requiredValue.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Дата получения
                if (achievement.isUnlocked && achievement.unlockedAt != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Получено: ${achievement.unlockedAt}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка закрытия
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Закрыть")
                }
            }
        }
    }
}
