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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api

/**
 * @param icons РЎРїРёСЃРѕРє РёРєРѕРЅРѕРє.
 * @param labels РЎРїРёСЃРѕРє РїРѕРґРїРёСЃРµР№.
 * @param subMenus РЎРїРёСЃРѕРє РїРѕРґРјРµРЅСЋ.
 * @param onClick Callback РЅР°Р¶Р°С‚РёСЏ.
 * @param onSubClick Callback РїРѕРґРјРµРЅСЋ.
 * @param modifier РњРѕРґРёС„РёРєР°С‚РѕСЂ Compose.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun iconMenuGrid(
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
                expandableMenu(
                    title = labels[index],
                    subIcons = subMenus[index],
                    onSubClick = { subIdx -> onSubClick(index, subIdx) },
                    icon = icon
                )
            } else {
                animatedMenuIcon(icon, labels[index]) { onClick(index) }
            }
        }
    }
}

/**
 * РђРЅРёРјРёСЂРѕРІР°РЅРЅР°СЏ РёРєРѕРЅРєР° РјРµРЅСЋ.
 * @param icon РРєРѕРЅРєР°.
 * @param label РџРѕРґРїРёСЃСЊ.
 * @param onClick Callback РЅР°Р¶Р°С‚РёСЏ.
 */
@Composable
fun animatedMenuIcon(icon: ImageVector, label: String, onClick: () -> Unit) {
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

/**
 * Р’С‹РїР°РґР°СЋС‰РµРµ РїРѕРґРјРµРЅСЋ РґР»СЏ РёРєРѕРЅРєРё.
 * @param title Р—Р°РіРѕР»РѕРІРѕРє.
 * @param subIcons РЎРїРёСЃРѕРє РёРєРѕРЅРѕРє РїРѕРґРјРµРЅСЋ.
 * @param onSubClick Callback РЅР°Р¶Р°С‚РёСЏ РЅР° РїРѕРґРјРµРЅСЋ.
 * @param icon РРєРѕРЅРєР°.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun expandableMenu(
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

@Composable
fun IconMenuGrid(
    icons: List<Any> = emptyList(),
    labels: List<String> = emptyList(),
    onClick: (Int) -> Unit = {}
) {
    // TODO: Реализовать компонент меню
}
