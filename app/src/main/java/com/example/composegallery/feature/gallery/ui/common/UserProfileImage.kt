package com.example.composegallery.feature.gallery.ui.common

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
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

    val sharedModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null && sharedKey != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
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
            .clip(CircleShape)
            .then(sharedModifier)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .shimmer(shimmer)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            )
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            onState = { state ->
                isLoading = state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Empty
            }
        )
    }
}

