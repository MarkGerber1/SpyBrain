package com.example.spybrain.presentation.breathing.patternbuilder

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
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloat
import androidx.compose.material3.ExperimentalMaterial3Api

/**
 * Р­РЅР°СЂ СЂРµРґР°РєС‚РёСЂРѕРІР°РЅРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЃРєРѕРіРѕ РґС‹С…Р°С‚РµР»СЊРЅРѕРіРѕ РїР°С‚С‚РµСЂРЅР°.
 * @param navController РљРѕРЅС‚СЂРѕР»Р»РµСЂ РЅР°РІРёРіР°С†РёРё.
 * @param patternId РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїР°С‚С‚РµСЂРЅР°.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editCustomBreathingPatternScreen(
    navController: NavController,
    patternId: String
) {
    val viewModel: BreathingPatternBuilderViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val patternIdLong = patternId.toLongOrNull() ?: 0L
    LaunchedEffect(patternIdLong) {
        viewModel.handleEvent(BreathingPatternBuilderContract.Event.LoadPattern(patternIdLong))
    }

    // РђРЅРёРјР°С†РёСЏ РґР»СЏ С„РѕРЅР°
    val infiniteTransition = rememberInfiniteTransition()
    val backgroundAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // РћР±СЂР°Р±РѕС‚РєР° СЌС„С„РµРєС‚РѕРІ СЃ С‚Р°РєС‚РёР»СЊРЅРѕР№ РѕР±СЂР°С‚РЅРѕР№ СЃРІСЏР·СЊСЋ
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BreathingPatternBuilderContract.Effect.ShowError -> {
                    VibrationUtil.vibrateError(context)
                    Toast.makeText(context, when(val err = effect.error) {
                        is UiError.Custom -> err.message
                        is UiError.NetworkError -> "РћС€РёР±РєР° СЃРµС‚Рё"
                        is UiError.ValidationError -> "РћС€РёР±РєР° РІР°Р»РёРґР°С†РёРё"
                        is UiError.UnknownError -> "РќРµРёР·РІРµСЃС‚РЅР°СЏ РѕС€РёР±РєР°"
                        else -> "РћС€РёР±РєР°"
                    }, Toast.LENGTH_SHORT).show()
                }
                is BreathingPatternBuilderContract.Effect.ShowSuccessMessage -> {
                    VibrationUtil.vibrateSuccess(context)
                    Toast.makeText(context, "РР·РјРµРЅРµРЅРёСЏ СЃРѕС…СЂР°РЅРµРЅС‹!", Toast.LENGTH_SHORT).show()
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
            // Р—Р°РіРѕР»РѕРІРѕРє СЃ Р°РЅРёРјР°С†РёРµР№
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
                                text = "Р РµРґР°РєС‚РёСЂРѕРІР°РЅРёРµ С€Р°Р±Р»РѕРЅР°",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "РќР°СЃС‚СЂРѕР№С‚Рµ РїР°СЂР°РјРµС‚СЂС‹ РґС‹С…Р°С‚РµР»СЊРЅРѕРіРѕ СѓРїСЂР°Р¶РЅРµРЅРёСЏ",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // РћСЃРЅРѕРІРЅС‹Рµ РїР°СЂР°РјРµС‚СЂС‹
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
                                text = "РћСЃРЅРѕРІРЅС‹Рµ РїР°СЂР°РјРµС‚СЂС‹",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = state.name,
                                onValueChange = {
                                    VibrationUtil.vibrateLight(context)
                                    viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterName(it))
                                },
                                label = { Text("РќР°Р·РІР°РЅРёРµ С€Р°Р±Р»РѕРЅР°") },
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
                                label = { Text("РћРїРёСЃР°РЅРёРµ (РѕРїС†РёРѕРЅР°Р»СЊРЅРѕ)") },
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

            // РџР°СЂР°РјРµС‚СЂС‹ РґС‹С…Р°РЅРёСЏ
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
                                text = "РџР°СЂР°РјРµС‚СЂС‹ РґС‹С…Р°РЅРёСЏ",
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

            // РљРЅРѕРїРєРё РґРµР№СЃС‚РІРёР№
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
