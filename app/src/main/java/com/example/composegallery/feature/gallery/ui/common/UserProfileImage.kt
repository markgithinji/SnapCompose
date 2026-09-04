package com.example.composegallery.feature.gallery.ui.common

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun UserProfileImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    sharedKey: String? = null
) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    var isLoading by remember { mutableStateOf(true) }
    
    // Track the last successful painter to prevent flicker during URL upgrades
    var lastSuccessfulPainter by remember { mutableStateOf<Painter?>(null) }

    val sharedModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null && sharedKey != null) {
        with(sharedTransitionScope) {
            Modifier
                // renderInSharedTransitionScopeOverlay ensures the image stays on top 
                // and isn't clipped by parent containers during the animation.
                .renderInSharedTransitionScopeOverlay(
                    zIndexInOverlay = 1f,
                    renderInOverlay = { true }
                )
                .sharedElement(
                    rememberSharedContentState(key = sharedKey),
                    animatedVisibilityScope = animatedVisibilityScope,
                    clipInOverlayDuringTransition = OverlayClip(CircleShape)
                )
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(CircleShape) // Container-level clip
            .then(sharedModifier)
    ) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape), // Content-level clip for safety
            onState = { state ->
                if (state is AsyncImagePainter.State.Success) {
                    lastSuccessfulPainter = state.painter
                }
                isLoading = state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Empty
            }
        ) {
            val state = painter.state
            
            // Keep the previous image visible while loading the new URL or during request reset
            val displayPainter = (state as? AsyncImagePainter.State.Success)?.painter 
                ?: (if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Empty) lastSuccessfulPainter else null)

            if (displayPainter != null) {
                Image(
                    painter = displayPainter,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Only show shimmer if we have no image at all
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .shimmer(shimmer)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
    }
}
