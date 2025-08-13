package com.example.spybrain.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.spybrain.R

@Composable
fun MediaControls(
    isPlaying: Boolean,
    isPaused: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.animateContentSize()
    ) {
        ControlCircle(
            icon = Icons.Filled.ArrowBack,
            tint = Color.White,
            description = stringResource(id = R.string.back),
            onClick = onBack,
            background = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            sizeDp = 48
        )

        ControlCircle(
            icon = Icons.Filled.Stop,
            tint = Color.White,
            description = stringResource(id = R.string.stop),
            onClick = onStop,
            background = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            sizeDp = 56
        )

        ControlCircle(
            icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            tint = Color.White,
            description = if (isPlaying) stringResource(id = R.string.pause) else stringResource(id = R.string.play),
            onClick = { if (isPlaying) onPause() else onPlay() },
            background = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            sizeDp = 72
        )
    }
}

@Composable
private fun ControlCircle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    description: String,
    onClick: () -> Unit,
    background: Color,
    sizeDp: Int
) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(background)
            .semantics {
                contentDescription = description
                role = Role.Button
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = description, tint = tint)
    }
}


