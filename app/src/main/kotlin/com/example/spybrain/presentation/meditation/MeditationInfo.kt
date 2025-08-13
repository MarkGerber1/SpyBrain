package com.example.spybrain

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.spybrain.R
import com.example.spybrain.domain.model.Meditation

@Composable
fun meditationInfoTabs(meditation: Meditation): List<Pair<String, String>> {
    val aboutLabel = stringResource(id = R.string.meditation_about)
    val howToLabel = stringResource(id = R.string.meditation_how_to)
    val (aboutText, howToText) = when (meditation.id) {
        "mindfulness_basics" -> (
            stringResource(id = R.string.meditation_about_mindfulness_basics) to
                stringResource(id = R.string.meditation_howto_mindfulness_basics)
            )
        "deep_breathing" -> (
            stringResource(id = R.string.meditation_about_deep_breathing) to
                stringResource(id = R.string.meditation_howto_deep_breathing)
            )
        "body_scan" -> (
            stringResource(id = R.string.meditation_about_body_scan) to
                stringResource(id = R.string.meditation_howto_body_scan)
            )
        else -> (
            stringResource(id = R.string.meditation_about_text) to
                stringResource(id = R.string.meditation_how_to_text)
            )
    }
    return listOf(
        aboutLabel to aboutText,
        howToLabel to howToText
    )
}


