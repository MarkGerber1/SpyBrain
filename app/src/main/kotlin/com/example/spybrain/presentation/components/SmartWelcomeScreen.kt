package com.example.spybrain.presentation.components

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Alignment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spybrain.R
import java.util.Calendar
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.unit.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.size
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spybrain.data.datastore.SettingsDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextFieldDefaults

/**
 * Экран умного приветствия пользователя.
 * @param isOffline Признак оффлайн-режима.
 * @param onQuickAction Callback для быстрых действий.
 */
@Composable
fun SmartWelcomeScreen(
    isOffline: Boolean = false,
    onQuickAction: (String) -> Unit = {},
    navController: NavHostController? = null
) {
    val context = LocalContext.current
    val settings = remember { SettingsDataStore(context) }
    val onboarded by settings.onboardedFlow.collectAsState(initial = false)

    if (!onboarded) {
        OnboardingForm(
            onSubmit = { name, age, gender ->
                // Save and mark onboarded
                LaunchedEffect(name, age, gender) {
                    settings.setUserName(name)
                    settings.setUserAge(age)
                    settings.setUserGender(gender)
                    settings.setOnboarded(true)
                    // Navigate to home
                    navController?.navigate(com.example.spybrain.presentation.navigation.Screen.Home.route) {
                        popUpTo(0)
                    }
                }
            }
        )
        return
    }

    var isNetworkAvailable by remember { mutableStateOf(!isOffline) }

    LaunchedEffect(Unit) {
        while (true) {
            isNetworkAvailable = isNetworkConnected(context)
            delay(5000)
        }
    }

    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val (greeting, bgRes) = when (hour) {
        in 5..11 -> "Доброе утро!" to com.example.spybrain.R.drawable.bg_water
        in 12..17 -> "Добрый день!" to com.example.spybrain.R.drawable.bg_nature
        in 18..22 -> "Добрый вечер!" to com.example.spybrain.R.drawable.bg_space
        else -> "Доброй ночи!" to com.example.spybrain.R.drawable.bg_air
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(greeting, style = MaterialTheme.typography.headlineLarge, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            if (!isNetworkAvailable) {
                Text("Нет соединения с сетью", color = Color.Red)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { onQuickAction("breathing") }) { Text("Быстрое дыхание") }
                    Button(onClick = { onQuickAction("meditation") }) { Text("Быстрая медитация") }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            var progress by remember { mutableStateOf(0f) }

            LaunchedEffect(Unit) {
                while(true) {
                    for (i in 0..100) {
                        progress = i / 100f
                        delay(50)
                    }
                    delay(1000)
                }
            }

            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(120.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun OnboardingForm(
    onSubmit: (name: String, age: Int?, gender: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Добро пожаловать!", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            singleLine = true
        )
        OutlinedTextField(
            value = ageText,
            onValueChange = { ageText = it.filter { ch -> ch.isDigit() }.take(3) },
            label = { Text("Возраст") },
            singleLine = true
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = gender ?: "Пол",
                onValueChange = {},
                readOnly = true,
                label = { Text("Пол") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(text = { Text("Мужской") }, onClick = { gender = "male"; expanded = false })
                DropdownMenuItem(text = { Text("Женский") }, onClick = { gender = "female"; expanded = false })
                DropdownMenuItem(text = { Text("Другое") }, onClick = { gender = "other"; expanded = false })
            }
        }
        Button(
            onClick = { onSubmit(name.trim(), ageText.toIntOrNull(), gender) },
            enabled = name.trim().isNotEmpty()
        ) { Text("Продолжить") }
    }
}

/**
 * Проверяет наличие интернет-соединения.
 * @param context Контекст.
 * @return true, если есть интернет.
 */
fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
           capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
