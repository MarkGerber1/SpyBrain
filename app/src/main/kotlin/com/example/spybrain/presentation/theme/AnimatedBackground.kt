package com.example.spybrain.presentation.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.*
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    var alpha: Float = 1f
)

@Composable
fun AnimatedBackground(
    themeKey: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        when (themeKey) {
            "water" -> drawWaterAnimation(time, size.width, size.height)
            "space" -> drawSpaceAnimation(time, size.width, size.height) 
            else -> drawNatureAnimation(time, size.width, size.height)
        }
    }
}

private fun DrawScope.drawWaterAnimation(time: Float, width: Float, height: Float) {
    // Фон градиент воды
    drawRect(
        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(
                Color(0xFF4FC3F7), // Светло-голубой
                Color(0xFF1976D2)  // Синий
            )
        ),
        size = size
    )
    
    // Волны
    val waveCount = 5
    for (i in 0 until waveCount) {
        val yOffset = height * 0.3f + i * 100f
        val amplitude = 80f + i * 20f
        val frequency = 0.003f + i * 0.001f
        val phase = time * 0.01f + i * 0.5f
        
        val path = Path().apply {
            moveTo(0f, yOffset)
            for (x in 0..width.toInt() step 10) {
                val y = yOffset + sin((x * frequency + phase) * 2 * PI).toFloat() * amplitude
                lineTo(x.toFloat(), y)
            }
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        
        drawPath(
            path = path,
            color = Color(0xFF0288D1).copy(alpha = 0.3f - i * 0.05f)
        )
    }
    
    // Пузырьки
    val bubbleCount = 15
    for (i in 0 until bubbleCount) {
        val bubbleTime = (time + i * 50f) % 1000f
        val x = (width * 0.1f + i * width * 0.8f / bubbleCount) + 
                sin(bubbleTime * 0.02f + i) * 50f
        val y = height - (bubbleTime / 1000f) * height * 1.2f
        val size = 15f + sin(bubbleTime * 0.05f + i) * 10f
        val alpha = 1f - (bubbleTime / 1000f)
        
        if (y > -100f) {
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.7f),
                radius = size,
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
            drawCircle(
                color = Color(0xFF81D4FA).copy(alpha = alpha * 0.5f),
                radius = size * 0.7f,
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
    }
}

private fun DrawScope.drawSpaceAnimation(time: Float, width: Float, height: Float) {
    // Космический фон
    drawRect(
        brush = androidx.compose.ui.graphics.Brush.radialGradient(
            colors = listOf(
                Color(0xFF1A237E), // Темно-фиолетовый центр
                Color(0xFF000051)  // Почти черный по краям
            ),
            radius = width * 0.8f
        ),
        size = size
    )
    
    // Звезды
    val starCount = 50
    for (i in 0 until starCount) {
        val starTime = (time + i * 20f) % 1000f
        val twinkle = sin(starTime * 0.1f + i) * 0.5f + 0.5f
        val x = (i * 31f) % width // Псевдослучайное размещение
        val y = (i * 47f) % height
        val starSize = 2f + (i % 3) * 2f
        
        drawCircle(
            color = Color.White.copy(alpha = twinkle * 0.9f),
            radius = starSize * twinkle,
            center = androidx.compose.ui.geometry.Offset(x, y)
        )
        
        // Крестик у ярких звезд
        if (i % 7 == 0 && twinkle > 0.7f) {
            val crossSize = starSize * 3f
            drawLine(
                color = Color.White.copy(alpha = twinkle * 0.5f),
                start = androidx.compose.ui.geometry.Offset(x - crossSize, y),
                end = androidx.compose.ui.geometry.Offset(x + crossSize, y),
                strokeWidth = 1f
            )
            drawLine(
                color = Color.White.copy(alpha = twinkle * 0.5f),
                start = androidx.compose.ui.geometry.Offset(x, y - crossSize),
                end = androidx.compose.ui.geometry.Offset(x, y + crossSize),
                strokeWidth = 1f
            )
        }
    }
    
    // Комета
    val cometTime = (time * 0.5f) % 1000f
    if (cometTime < 300f) { // Комета видна 30% времени
        val progress = cometTime / 300f
        val cometX = -100f + progress * (width + 200f)
        val cometY = 100f + progress * 200f
        
        // Хвост кометы
        val tailLength = 150f
        for (j in 0 until 20) {
            val tailProgress = j / 20f
            val tailX = cometX - tailProgress * tailLength
            val tailY = cometY - tailProgress * 50f
            val tailAlpha = (1f - tailProgress) * 0.8f
            val tailSize = (1f - tailProgress) * 8f
            
            drawCircle(
                color = Color(0xFFFFEB3B).copy(alpha = tailAlpha),
                radius = tailSize,
                center = androidx.compose.ui.geometry.Offset(tailX, tailY)
            )
        }
        
        // Голова кометы
        drawCircle(
            color = Color(0xFFFFEB3B),
            radius = 12f,
            center = androidx.compose.ui.geometry.Offset(cometX, cometY)
        )
        drawCircle(
            color = Color.White,
            radius = 6f,
            center = androidx.compose.ui.geometry.Offset(cometX, cometY)
        )
    }
}

private fun DrawScope.drawNatureAnimation(time: Float, width: Float, height: Float) {
    // Природный фон
    drawRect(
        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(
                Color(0xFF81C784), // Светло-зеленый
                Color(0xFF388E3C)  // Темно-зеленый
            )
        ),
        size = size
    )
    
    // Падающие листья
    val leafCount = 20
    for (i in 0 until leafCount) {
        val leafTime = (time + i * 50f) % 1000f
        val progress = leafTime / 1000f
        val x = (i * 53f) % width + sin(leafTime * 0.02f + i) * 100f
        val y = -50f + progress * (height + 100f)
        val rotation = leafTime * 0.05f + i
        val leafSize = 8f + (i % 3) * 4f
        val alpha = 1f - abs(progress - 0.5f) * 2f
        
        if (y < height + 50f) {
            // Листик как овал
            val leafColor = when (i % 3) {
                0 -> Color(0xFF4CAF50)
                1 -> Color(0xFF8BC34A) 
                else -> Color(0xFF689F38)
            }
            
            drawCircle(
                color = leafColor.copy(alpha = alpha * 0.8f),
                radius = leafSize,
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
            
            // Прожилка листа
            val veinEnd = androidx.compose.ui.geometry.Offset(
                x + cos(rotation) * leafSize * 0.8f,
                y + sin(rotation) * leafSize * 0.8f
            )
            drawLine(
                color = Color(0xFF2E7D32).copy(alpha = alpha * 0.6f),
                start = androidx.compose.ui.geometry.Offset(x, y),
                end = veinEnd,
                strokeWidth = 1.5f
            )
        }
    }
    
    // Светлячки
    val firefliesCount = 8
    for (i in 0 until firefliesCount) {
        val fireflyTime = time + i * 125f
        val x = width * 0.2f + (i * width * 0.6f / firefliesCount) + 
               sin(fireflyTime * 0.01f + i) * 100f
        val y = height * 0.3f + sin(fireflyTime * 0.008f + i * 2f) * 200f
        val glow = (sin(fireflyTime * 0.05f + i) * 0.5f + 0.5f)
        
        // Свечение светлячка
        for (j in 0 until 5) {
            val glowRadius = (j + 1) * 6f
            val glowAlpha = glow * (1f - j * 0.2f) * 0.3f
            drawCircle(
                color = Color(0xFFFFEB3B).copy(alpha = glowAlpha),
                radius = glowRadius,
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
        
        // Тело светлячка
        drawCircle(
            color = Color(0xFFFFEB3B).copy(alpha = glow),
            radius = 3f,
            center = androidx.compose.ui.geometry.Offset(x, y)
        )
    }
}

