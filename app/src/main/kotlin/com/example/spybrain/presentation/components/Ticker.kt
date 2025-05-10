package com.example.spybrain.presentation.components

import android.text.TextUtils
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.toArgb

/**
 * Компонент бегущей строки (Ticker) для показа динамических советов
 */
@Composable
fun Ticker(
    text: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                setTextColor(Color.White.toArgb())
                setBackgroundColor(Color.Black.toArgb())
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.MARQUEE
                marqueeRepeatLimit = -1
                isSelected = true
                textSize = 16f
                setPadding(8, 8, 8, 8)
            }
        },
        update = { view ->
            view.text = text
        },
        modifier = modifier
    )
} 