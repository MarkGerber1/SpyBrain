// FIXME UI/UX финал 09.05.2025
package com.example.spybrain.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.layout.padding
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IconMenuGrid(
    icons: List<ImageVector>,
    labels: List<String>,
    subMenus: List<List<Pair<ImageVector, String>>>? = null,
    onClick: (Int) -> Unit = {},
    onSubClick: (Int, Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        itemsIndexed(icons) { index, icon ->
            if (subMenus != null && subMenus.getOrNull(index)?.isNotEmpty() == true) {
                ExpandableMenu(
                    title = labels[index],
                    subIcons = subMenus[index],
                    onSubClick = { subIdx -> onSubClick(index, subIdx) },
                    icon = icon
                )
            } else {
                AnimatedMenuIcon(icon, labels[index]) { onClick(index) }
            }
        }
    }
}

// TODO реализовано наплыв-иконок
@Composable
fun AnimatedMenuIcon(icon: ImageVector, label: String, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 1.15f else 1f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                        onClick()
                    }
                )
            }
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(32.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

// FIXME UI/UX финал 09.05.2025
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableMenu(
    title: String,
    subIcons: List<Pair<ImageVector, String>>,
    onSubClick: (Int) -> Unit,
    icon: ImageVector
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.menuAnchor()
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
            Text(title, modifier = Modifier.padding(start = 8.dp))
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            subIcons.forEachIndexed { idx, (subIcon, subLabel) ->
                DropdownMenuItem(
                    text = { Text(subLabel) },
                    leadingIcon = { Icon(subIcon, contentDescription = subLabel) },
                    onClick = { onSubClick(idx); expanded = false }
                )
            }
        }
    }
} 