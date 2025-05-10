package com.example.spybrain.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val profile = state.profile

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        if (profile != null) {
            Text(text = "Имя: ${profile.name}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Дней подряд: ${profile.streakDays}", style = MaterialTheme.typography.bodyMedium)
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            Text(text = "Зарегистрирован: ${sdf.format(profile.joinDate)}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.setEvent(ProfileContract.Event.EditNameClicked) }) {
                Text("Изменить имя")
            }
        } else {
            Text("Загрузка профиля...", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }

    if (state.showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setEvent(ProfileContract.Event.DismissDialog) },
            confirmButton = {
                TextButton(onClick = { viewModel.setEvent(ProfileContract.Event.SaveName) }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setEvent(ProfileContract.Event.DismissDialog) }) {
                    Text("Отмена")
                }
            },
            title = { Text("Изменить имя") },
            text = {
                OutlinedTextField(
                    value = state.newName,
                    onValueChange = { viewModel.setEvent(ProfileContract.Event.NameChanged(it)) },
                    label = { Text("Новое имя") }
                )
            }
        )
    }
} 