import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun EditCustomBreathingPatternScreen(
    navController: NavController,
    patternId: String
) {
    val viewModel = hiltViewModel<BreathingPatternBuilderViewModel>()
    val state by viewModel.state.collectAsState()

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