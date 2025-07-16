package com.example.spybrain.presentation.breathing.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.runtime.mutableStateListOf

/**
 * РђРЅРёРјР°С†РёСЏ СЃРµСЂРґС†РµР±РёРµРЅРёСЏ РґР»СЏ РІРёР·СѓР°Р»РёР·Р°С†РёРё BPM.
 * @param bpm РљРѕР»РёС‡РµСЃС‚РІРѕ СѓРґР°СЂРѕРІ РІ РјРёРЅСѓС‚Сѓ.
 * @param size Р Р°Р·РјРµСЂ РёРєРѕРЅРєРё.
 */
@Composable
fun HeartBeatAnimation(
    bpm: Int,
    size: Dp
) {
    var pulse by remember { mutableStateOf(false) }
    LaunchedEffect(bpm) {
        while (true) {
            pulse = !pulse
            delay((60000L / bpm).coerceAtLeast(100L))
        }
    }
    val scale by animateFloatAsState(
        targetValue = if (pulse) 1.3f else 1f,
        animationSpec = infiniteRepeatable(animation = tween(((60000L / bpm) / 2).toInt().coerceAtLeast(100)))
    )
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .graphicsLayer { scaleX = scale; scaleY = scale },
            tint = Color.Red
        )
    }
}

/**
 * Р’РёР·СѓР°Р»РёР·Р°С†РёСЏ Р­РљР“-РіСЂР°С„РёРєР°.
 * @param modifier РњРѕРґРёС„РёРєР°С‚РѕСЂ.
 * @param pointCount РљРѕР»РёС‡РµСЃС‚РІРѕ С‚РѕС‡РµРє РіСЂР°С„РёРєР°.
 */
@Composable
fun EcgGraph(
    modifier: Modifier = Modifier,
    pointCount: Int = 50
) {
    val points = remember { mutableStateListOf<Float>() }
    LaunchedEffect(Unit) {
        while (true) {
            if (points.size >= pointCount) points.removeAt(0)
            points.add(Random.nextFloat())
            delay(200L)
        }
    }
    Canvas(modifier = modifier.size(200.dp, 100.dp)) {
        val w = size.width
        val h = size.height
        val stepX = if (pointCount > 1) w / (pointCount - 1) else 0f
        points.forEachIndexed { i, v ->
            if (i > 0) {
                val x1 = stepX * (i - 1)
                val y1 = h * (1f - points[i - 1])
                val x2 = stepX * i
                val y2 = h * (1f - v)
                drawLine(Color.Red, Offset(x1, y1), Offset(x2, y2), strokeWidth = 3f)
            }
        }
    }
}

fun bioVisualization(/* параметры */) {
    // ... existing code ...
}

fun bioChart(/* параметры */) {
    // ... existing code ...
}
