package com.example.spybrain.presentation.breathing.patternbuilder

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.spybrain.R
import com.example.spybrain.util.UiError
import com.example.spybrain.util.VibrationUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingPatternBuilderScreen(
    navController: NavController,
    viewModel: BreathingPatternBuilderViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    
    // Анимация для фона
    val infiniteTransition = rememberInfiniteTransition()
    val backgroundAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Обработка эффектов с тактильной обратной связью
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BreathingPatternBuilderContract.Effect.ShowError -> {
                    VibrationUtil.vibrateError(context)
                    Toast.makeText(context, when(val err = effect.error) {
                        is UiError.Custom -> err.message
                        is UiError.NetworkError -> "Ошибка сети"
                        is UiError.ValidationError -> "Ошибка валидации"
                        is UiError.UnknownError -> "Неизвестная ошибка"
                        else -> "Ошибка"
                    }, Toast.LENGTH_SHORT).show()
                }
                is BreathingPatternBuilderContract.Effect.ShowSuccessMessage -> {
                    VibrationUtil.vibrateSuccess(context)
                    Toast.makeText(context, "Шаблон сохранен!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
            // Заголовок с анимацией
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
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Создание дыхательного шаблона",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Настройте свой уникальный ритм дыхания",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Форма создания шаблона
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
                                text = "Основные параметры",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            OutlinedTextField(
                                value = state.name,
                                onValueChange = { 
                                    VibrationUtil.vibrateLight(context)
                                    viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterName(it)) 
                                },
                                label = { Text("Название шаблона") },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            
                            OutlinedTextField(
                                value = state.description,
                                onValueChange = { 
                                    viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterDescription(it)) 
                                },
                                label = { Text("Описание (опционально)") },
                                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    }
                }
            }

            // Параметры дыхания
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
                                text = "Параметры дыхания",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = state.inhaleSeconds,
                                    onValueChange = { 
                                        VibrationUtil.vibrateLight(context)
                                        viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterInhale(it)) 
                                    },
                                    label = { Text(stringResource(R.string.inhale_seconds)) },
                                    leadingIcon = { Icon(Icons.Default.Air, contentDescription = null) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4CAF50),
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                
                                OutlinedTextField(
                                    value = state.holdAfterInhaleSeconds,
                                    onValueChange = { 
                                        viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterHoldInhale(it)) 
                                    },
                                    label = { Text(stringResource(R.string.pause_seconds)) },
                                    leadingIcon = { Icon(Icons.Default.Pause, contentDescription = null) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFF9800),
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = state.exhaleSeconds,
                                    onValueChange = { 
                                        VibrationUtil.vibrateLight(context)
                                        viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterExhale(it)) 
                                    },
                                    label = { Text(stringResource(R.string.exhale_seconds)) },
                                    leadingIcon = { Icon(Icons.Default.Air, contentDescription = null) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFE91E63),
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                
                                OutlinedTextField(
                                    value = state.holdAfterExhaleSeconds,
                                    onValueChange = { 
                                        viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterHoldExhale(it)) 
                                    },
                                    label = { Text(stringResource(R.string.pause_seconds)) },
                                    leadingIcon = { Icon(Icons.Default.Pause, contentDescription = null) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFF9800),
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                            }
                            
                            OutlinedTextField(
                                value = state.totalCycles,
                                onValueChange = { 
                                    VibrationUtil.vibrateLight(context)
                                    viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterCycles(it)) 
                                },
                                label = { Text(stringResource(R.string.cycles_count)) },
                                leadingIcon = { Icon(Icons.Default.Repeat, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            
                            Button(
                                onClick = { 
                                    VibrationUtil.vibrateSuccess(context)
                                    viewModel.setEvent(BreathingPatternBuilderContract.Event.SavePattern) 
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.save_changes), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Список сохранённых шаблонов
            if (state.patterns.isNotEmpty()) {
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
                                    text = "Сохранённые шаблоны",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(state.patterns) { pattern ->
                                        AnimatedVisibility(
                                            visible = true,
                                            enter = fadeIn() + expandVertically(),
                                            modifier = Modifier.animateContentSize()
                                        ) {
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        VibrationUtil.vibrateLight(context)
                                                        // TODO: Добавить редактирование шаблона
                                                    },
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                                ),
                                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(16.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            pattern.name,
                                                            style = MaterialTheme.typography.titleSmall,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        if (!pattern.description.isNullOrBlank()) {
                                                            Text(
                                                                pattern.description.orEmpty(),
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            Text(
                                                                "Вдох ${pattern.inhaleSeconds}с",
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = Color(0xFF4CAF50)
                                                            )
                                                            Text(
                                                                "Выдох ${pattern.exhaleSeconds}с",
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = Color(0xFFE91E63)
                                                            )
                                                            Text(
                                                                "Циклов: ${pattern.totalCycles}",
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                        }
                                                    }
                                                    
                                                    IconButton(
                                                        onClick = { 
                                                            VibrationUtil.vibrateError(context)
                                                            viewModel.setEvent(BreathingPatternBuilderContract.Event.DeletePattern(pattern)) 
                                                        }
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Delete,
                                                            contentDescription = "Удалить шаблон",
                                                            tint = Color(0xFFE53935)
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
            }
        }
    }
} 