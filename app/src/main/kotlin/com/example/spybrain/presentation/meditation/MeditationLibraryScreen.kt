package com.example.spybrain.presentation.meditation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spybrain.presentation.base.UiEffect
import com.example.spybrain.util.UiError
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MeditationLibraryScreen(
    viewModel: MeditationLibraryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MeditationLibraryContract.Effect.ShowError ->
                    Toast.makeText(context, when(val err = effect.error) {
                        is UiError.Custom -> err.message
                        is UiError.NetworkError -> "Ошибка сети"
                        is UiError.ValidationError -> "Ошибка валидации"
                        is UiError.UnknownError -> "Неизвестная ошибка"
                        else -> "Ошибка"
                    }, Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (state.programs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // FIXME UI/UX финал 09.05.2025: Используем IconMenuGrid вместо LazyColumn для программ медитации
        val programIcons = List(state.programs.size) { Icons.Default.SelfImprovement } // FIXME билд-фикс 09.05.2025
        val programLabels = state.programs.map { it.title }
        com.example.spybrain.presentation.components.IconMenuGrid(
            icons = programIcons, // FIXME билд-фикс 09.05.2025
            labels = programLabels,
            onClick = { idx -> viewModel.playProgram(state.programs[idx]) }
        )
    }
} 