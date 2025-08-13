package com.example.spybrain.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    title: String,
    tabs: List<Pair<String, String>>, // label to content
    onDismiss: () -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .animateContentSize()
        ) {
            Text(text = title, modifier = Modifier.semantics { contentDescription = title })

            if (tabs.isNotEmpty()) {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, (label, _) ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(text = label) }
                        )
                    }
                }

                val content = tabs[selectedTabIndex].second
                Text(
                    text = content,
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                )
            }
        }
    }
}




