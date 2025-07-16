package com.example.spybrain.presentation.meditation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.spybrain.presentation.components.IconMenuGrid
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spybrain.presentation.meditation.MeditationLibraryContract
import com.example.spybrain.presentation.meditation.MeditationLibraryViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.spybrain.util.UiError

@Composable
fun meditationLibraryScreen(
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
                        is UiError.NetworkError -> "РћС€РёР±РєР° СЃРµС‚Рё"
                        is UiError.ValidationError -> "РћС€РёР±РєР° РІР°Р»РёРґР°С†РёРё"
                        is UiError.UnknownError -> "РќРµРёР·РІРµСЃС‚РЅР°СЏ РѕС€РёР±РєР°"
                        else -> "РћС€РёР±РєР°"
                    }, Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (state.programs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // FIXME UI/UX С„РёРЅР°Р» 09.05.2025: РСЃРїРѕР»СЊР·СѓРµРј IconMenuGrid РІРјРµСЃС‚Рѕ LazyColumn РґР»СЏ РїСЂРѕРіСЂР°РјРј РјРµРґРёС‚Р°С†РёРё
        val programIcons = List(state.programs.size) { Icons.Default.SelfImprovement } // FIXME Р±РёР»Рґ-С„РёРєСЃ 09.05.2025
        val programLabels = state.programs.map { it.title }
        com.example.spybrain.presentation.components.IconMenuGrid(
            icons = programIcons, // FIXME Р±РёР»Рґ-С„РёРєСЃ 09.05.2025
            labels = programLabels,
            onClick = { idx -> viewModel.playProgram(state.programs[idx]) }
        )
    }
}
