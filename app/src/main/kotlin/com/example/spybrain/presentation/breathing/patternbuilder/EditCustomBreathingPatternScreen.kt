package com.example.spybrain.presentation.breathing.patternbuilder

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.spybrain.R
import com.example.spybrain.util.UiError
import com.example.spybrain.util.VibrationUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCustomBreathingPatternScreen(
    navController: NavController,
    patternId: String
) {
    val viewModel: BreathingPatternBuilderViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Анимация для фона
    val infiniteTransition = rememberInfiniteTransition()
    val backgroundAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
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
                    Toast.makeText(context, "Изменения сохранены!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
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
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Редактирование шаблона",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Настройте параметры дыхательного упражнения",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Основные параметры
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
                                minLines = 2,
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
                        }
                    }
                }
            }

            // Кнопки действий
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
                            
                            OutlinedButton(
                                onClick = { 
                                    VibrationUtil.vibrateLight(context)
                                    navController.popBackStack() 
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    }
                }
            }
        }
    }
} 