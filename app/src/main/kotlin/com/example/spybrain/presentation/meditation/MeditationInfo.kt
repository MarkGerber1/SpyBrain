package com.example.spybrain

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.spybrain.R
import com.example.spybrain.domain.model.Meditation

@Composable
fun meditationInfoTabs(meditation: Meditation): List<Pair<String, String>> {
    val aboutLabel = stringResource(id = R.string.meditation_about)
    val howToLabel = stringResource(id = R.string.meditation_how_to)
    val normalizedId = meditation.id.removePrefix("meditation_")
    val (aboutText, howToText) = when (normalizedId) {
        "fire" -> (
            stringResource(id = R.string.meditation_fire_about) to
                stringResource(id = R.string.meditation_fire_howto)
            )
        "forest_spirit" -> (
            stringResource(id = R.string.meditation_forest_about) to
                stringResource(id = R.string.meditation_forest_howto)
            )
        "night_sky" -> (
            stringResource(id = R.string.meditation_night_sky_about) to
                stringResource(id = R.string.meditation_night_sky_howto)
            )
        "angelic" -> (
            stringResource(id = R.string.meditation_angelic_about) to
                stringResource(id = R.string.meditation_angelic_howto)
            )
        "dreaming" -> (
            stringResource(id = R.string.meditation_dreaming_about) to
                stringResource(id = R.string.meditation_dreaming_howto)
            )
        "relaxation" -> (
            stringResource(id = R.string.meditation_relaxation_about) to
                stringResource(id = R.string.meditation_relaxation_howto)
            )
        "spiritual" -> (
            stringResource(id = R.string.meditation_spiritual_about) to
                stringResource(id = R.string.meditation_spiritual_howto)
            )
        "valley_sunset" -> (
            stringResource(id = R.string.meditation_valley_sunset_about) to
                stringResource(id = R.string.meditation_valley_sunset_howto)
            )
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

@Composable
fun meditationDisplayTitle(meditation: Meditation): String {
    val key = meditation.id.removePrefix("meditation_")
    return when (key) {
        "fire" -> stringResource(id = R.string.meditation_fire_name)
        "forest_spirit" -> stringResource(id = R.string.meditation_forest_name)
        "night_sky" -> stringResource(id = R.string.meditation_night_sky_name)
        "angelic" -> stringResource(id = R.string.meditation_angelic_name)
        "dreaming" -> stringResource(id = R.string.meditation_dreaming_name)
        "relaxation" -> stringResource(id = R.string.meditation_relaxation_name)
        "spiritual" -> stringResource(id = R.string.meditation_spiritual_name)
        "valley_sunset" -> stringResource(id = R.string.meditation_valley_sunset_name)
        else -> meditation.title
    }
}

@Composable
fun meditationDisplaySubtitle(meditation: Meditation): String? {
    val key = meditation.id.removePrefix("meditation_")
    return when (key) {
        "fire" -> stringResource(id = R.string.meditation_fire_subtitle)
        "forest_spirit" -> stringResource(id = R.string.meditation_forest_subtitle)
        "night_sky" -> stringResource(id = R.string.meditation_night_sky_subtitle)
        "angelic" -> stringResource(id = R.string.meditation_angelic_subtitle)
        "dreaming" -> stringResource(id = R.string.meditation_dreaming_subtitle)
        "relaxation" -> stringResource(id = R.string.meditation_relaxation_subtitle)
        "spiritual" -> stringResource(id = R.string.meditation_spiritual_subtitle)
        "valley_sunset" -> stringResource(id = R.string.meditation_valley_sunset_subtitle)
        else -> meditation.description
    }
}


