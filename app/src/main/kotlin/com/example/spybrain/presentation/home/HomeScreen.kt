package com.example.spybrain.presentation.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spybrain.presentation.theme.DynamicBackground
import com.example.spybrain.presentation.components.personalGreeting
import com.example.spybrain.presentation.navigation.Screen
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.runtime.collectAsState

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    DynamicBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                personalGreeting(userName = state.userName)

                // Neural tabs area
                NeuralTabs(
                    onMeditationClick = { navController.navigate(Screen.Meditation.route) },
                    onBreathingClick = { navController.navigate(Screen.Breathing.route) },
                    onStatsClick = { navController.navigate(Screen.Stats.route) }
                )

                Card(
                    modifier = Modifier.padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    )
                ) {
                    Text(
                        text = state.welcomeText,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    }
}

@Composable
private fun NeuralTabs(
    onMeditationClick: () -> Unit,
    onBreathingClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    var pulse by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            pulse = Random.nextFloat()
            delay(600)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(top = 120.dp)) {
        // Animated neural links background
        Canvas(modifier = Modifier.matchParentSize()) {
            val centerX = size.width / 2
            val topY = size.height * 0.15f
            val leftX = size.width * 0.2f
            val rightX = size.width * 0.8f
            val bottomY = size.height * 0.5f

            // Nodes
            val nodes = listOf(
                Offset(centerX, topY), // Meditation
                Offset(leftX, bottomY), // Breathing
                Offset(rightX, bottomY) // Stats
            )

            // Draw links with pulsing effect
            val color = Color(0xFF80D8FF).copy(alpha = 0.6f + 0.4f * pulse)
            val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 6f,
                pathEffect = PathEffect.cornerPathEffect(24f)
            )
            drawLine(color, nodes[0], nodes[1], strokeWidth = stroke.width)
            drawLine(color, nodes[0], nodes[2], strokeWidth = stroke.width)
            drawLine(color, nodes[1], nodes[2], strokeWidth = 2f)

            // Glowing dots
            nodes.forEach { point ->
                drawCircle(color = color, radius = 14f + 6f * pulse, center = point)
            }
        }

        // Tab buttons
        Column(modifier = Modifier.align(Alignment.TopCenter)) {
            Button(onClick = onMeditationClick) { Text("Медитация") }
        }
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Button(onClick = onBreathingClick) { Text("Дыхание") }
        }
        Column(modifier = Modifier.align(Alignment.CenterEnd)) {
            Button(onClick = onStatsClick) { Text("Статистика") }
        }
    }
}