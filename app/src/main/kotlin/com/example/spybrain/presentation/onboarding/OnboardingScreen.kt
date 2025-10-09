package com.example.spybrain.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Экран онбординга для ввода имени и даты рождения пользователя.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onOnboardingCompleted: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val effect by viewModel.effect.collectAsState(initial = null)

    // Обработка эффектов
    LaunchedEffect(effect) {
        effect?.let { eff ->
            when (eff) {
                is OnboardingContract.Effect.ShowValidationError -> {
                    // Показываем Snackbar с ошибкой
                }
                is OnboardingContract.Effect.CompleteOnboarding -> {
                    onOnboardingCompleted()
                }
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Заголовок
            Text(
                text = "Добро пожаловать в SpyBrain!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Поле ввода имени
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.setEvent(OnboardingContract.Event.NameEntered(it)) },
                label = { Text("Ваше имя") },
                placeholder = { Text("Введите ваше имя") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    autoCorrect = false
                ),
                isError = state.name.isNotBlank() && !state.isNameValid,
                supportingText = {
                    if (state.name.isNotBlank() && !state.isNameValid) {
                        Text("Имя должно содержать минимум 2 буквы")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Селектор даты рождения
            DateSelector(
                selectedDate = state.selectedDate,
                onDateSelected = { date ->
                    viewModel.setEvent(OnboardingContract.Event.DateSelected(date))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )

            // Кнопка продолжить
            Button(
                onClick = { viewModel.setEvent(OnboardingContract.Event.ContinueClicked) },
                enabled = state.canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Продолжить")
                }
            }
        }
    }
}

/**
 * Компонент селектора даты рождения с колесами прокрутки.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val currentDate = selectedDate ?: LocalDate.now().minusYears(25)

    Card(
        modifier = modifier,
        onClick = { showDatePicker = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Дата рождения",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "Выберите дату",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.DateRange,
                contentDescription = "Выбрать дату",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        // Здесь будет логика получения выбранной даты из DatePicker
                        // Пока используем текущую дату как заглушку
                        onDateSelected(currentDate)
                    }
                ) {
                    Text("Выбрать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            // В будущем здесь будет Material3 DatePicker
            // Пока показываем placeholder
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Выберите дату рождения",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Временный селектор с колесами
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumberWheel(
                        value = currentDate.dayOfMonth,
                        range = 1..31,
                        onValueChange = { /* Заглушка для будущего селектора */ }
                    )
                    Text(".")
                    NumberWheel(
                        value = currentDate.monthValue,
                        range = 1..12,
                        onValueChange = { /* Заглушка для будущего селектора */ }
                    )
                    Text(".")
                    NumberWheel(
                        value = currentDate.year,
                        range = 1950..2010,
                        onValueChange = { /* Заглушка для будущего селектора */ }
                    )
                }
            }
        }
    }
}

/**
 * Колесо прокрутки для чисел.
 */
@Composable
fun NumberWheel(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayValue = remember(value) {
        value.toString().padStart(2, '0')
    }

    Card(
        modifier = modifier.width(60.dp),
        onClick = {
            // В будущем здесь будет логика показа колеса прокрутки
            // Пока просто переключаем на следующее значение
            val nextValue = if (value < range.last) value + 1 else range.first
            onValueChange(nextValue)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayValue,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}