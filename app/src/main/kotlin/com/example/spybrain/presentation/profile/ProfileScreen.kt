package com.example.spybrain.presentation.profile

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api

/**
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val profile = state.profile

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        if (profile != null) {
            Text(text = "РРјСЏ: ${profile.name}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Р”РЅРµР№ РїРѕРґСЂСЏРґ: ${profile.streakDays}", style = MaterialTheme.typography.bodyMedium)
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            Text(text = "Р—Р°СЂРµРіРёСЃС‚СЂРёСЂРѕРІР°РЅ: ${sdf.format(profile.joinDate)}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.setEvent(ProfileContract.Event.EditNameClicked) }) {
                Text("РР·РјРµРЅРёС‚СЊ РёРјСЏ")
            }
        } else {
            Text("Р—Р°РіСЂСѓР·РєР° РїСЂРѕС„РёР»СЏ...", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }

    if (state.showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setEvent(ProfileContract.Event.DismissDialog) },
            confirmButton = {
                TextButton(onClick = { viewModel.setEvent(ProfileContract.Event.SaveName) }) {
                    Text("РЎРѕС…СЂР°РЅРёС‚СЊ")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setEvent(ProfileContract.Event.DismissDialog) }) {
                    Text("РћС‚РјРµРЅР°")
                }
            },
            title = { Text("РР·РјРµРЅРёС‚СЊ РёРјСЏ") },
            text = {
                OutlinedTextField(
                    value = state.newName,
                    onValueChange = { viewModel.setEvent(ProfileContract.Event.NameChanged(it)) },
                    label = { Text("РќРѕРІРѕРµ РёРјСЏ") }
                )
            }
        )
    }
}
