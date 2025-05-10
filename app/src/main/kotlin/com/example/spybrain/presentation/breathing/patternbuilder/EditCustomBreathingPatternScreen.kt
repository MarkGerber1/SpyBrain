package com.example.spybrain.presentation.breathing.patternbuilder

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.spybrain.presentation.breathing.patternbuilder.BreathingPatternBuilderContract
import com.example.spybrain.presentation.breathing.patternbuilder.BreathingPatternBuilderViewModel
import com.example.spybrain.util.UiError

@Composable
fun EditCustomBreathingPatternScreen(
    navController: NavController,
    patternId: String
) {
    val viewModel: BreathingPatternBuilderViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    androidx.compose.runtime.LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BreathingPatternBuilderContract.Effect.ShowError ->
                    Toast.makeText(context, when(val err = effect.error) {
                        is UiError.Custom -> err.message
                        is UiError.NetworkError -> "Ошибка сети"
                        is UiError.ValidationError -> "Ошибка валидации"
                        is UiError.UnknownError -> "Неизвестная ошибка"
                        else -> "Ошибка"
                    }, Toast.LENGTH_SHORT).show()
                is BreathingPatternBuilderContract.Effect.ShowSuccessMessage -> {
                    // ...
                }
                else -> {}
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterName(it)) },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterDescription(it)) },
                    label = { Text("Описание (опционально)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.inhaleSeconds,
                    onValueChange = { viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterInhale(it)) },
                    label = { Text("Время вдоха (сек)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.holdAfterInhaleSeconds,
                    onValueChange = { viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterHoldInhale(it)) },
                    label = { Text("Пауза после вдоха (сек)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.exhaleSeconds,
                    onValueChange = { viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterExhale(it)) },
                    label = { Text("Время выдоха (сек)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.holdAfterExhaleSeconds,
                    onValueChange = { viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterHoldExhale(it)) },
                    label = { Text("Пауза после выдоха (сек)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.totalCycles,
                    onValueChange = { viewModel.setEvent(BreathingPatternBuilderContract.Event.EnterCycles(it)) },
                    label = { Text("Количество циклов") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        viewModel.setEvent(BreathingPatternBuilderContract.Event.SavePattern)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить изменения")
                }
            }
        }
    }
} 