package com.example.composegallery.core.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import com.valentinilk.shimmer.defaultShimmerTheme

/**
 * A custom shimmer theme with increased wave speed for a snappier feel.
 * Default is usually 1000ms.
 */
val AppShimmerTheme = defaultShimmerTheme.copy(
    animationSpec = infiniteRepeatable(
        animation = tween(
            durationMillis = 800,
            easing = LinearEasing
        ),
        repeatMode = RepeatMode.Restart
    )
)
