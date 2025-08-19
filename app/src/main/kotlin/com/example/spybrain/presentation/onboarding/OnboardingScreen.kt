package com.example.spybrain.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spybrain.presentation.settings.SettingsViewModel
import com.example.spybrain.presentation.theme.DynamicBackground

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("other") }

    DynamicBackground {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Добро пожаловать в SpyBrain", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary)
                Text("Пару слов о вас — и начнём", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Имя") })
                OutlinedTextField(value = ageText, onValueChange = { ageText = it.filter { ch -> ch.isDigit() } }, label = { Text("Возраст") })

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GenderChip(selected = gender == "male", label = "Мужской") { gender = "male" }
                    GenderChip(selected = gender == "female", label = "Женский") { gender = "female" }
                    GenderChip(selected = gender == "other", label = "Другое") { gender = "other" }
                }

                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    val age = ageText.toIntOrNull() ?: 0
                    settingsViewModel.setEvent(com.example.spybrain.presentation.settings.SettingsContract.Event.UserNameChanged(name.trim()))
                    settingsViewModel.setEvent(com.example.spybrain.presentation.settings.SettingsContract.Event.UserAgeChanged(age))
                    settingsViewModel.setEvent(com.example.spybrain.presentation.settings.SettingsContract.Event.UserGenderChanged(gender))
                    onFinished()
                }, enabled = name.isNotBlank()) {
                    Text("Продолжить")
                }
            }
        }
    }
}

@Composable
private fun GenderChip(selected: Boolean, label: String, onClick: () -> Unit) {
    androidx.compose.material3.FilterChip(selected = selected, onClick = onClick, label = { Text(label) })
}


