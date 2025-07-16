package androidx.compose.animation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@Composable
fun InfiniteTransition.animateFloatAsState(
    initialValue: Float,
    targetValue: Float,
    animationSpec: InfiniteRepeatableSpec<Float>,
    label: String = ""
): State<Float> = animateFloat(
    initialValue = initialValue,
    targetValue = targetValue,
    animationSpec = animationSpec,
    label = label
)