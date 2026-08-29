package com.example.composegallery.feature.gallery.ui.common

import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.composegallery.feature.gallery.ui.util.BlurHashDecoder
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    blurHash: String? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    sharedKey: String? = null,
    placeholderUrl: String? = null,
    onLoading: ((Boolean) -> Unit)? = null,
    onSuccess: (() -> Unit)? = null,
    onRetry: () -> Unit,
) {
    val blurBitmap: ImageBitmap? = remember(blurHash) {
        blurHash?.let { BlurHashDecoder.decode(it, 20, 12)?.asImageBitmap() }
    }
    
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(imageUrl) {
        // Log.d("PhotoImage", "imageUrl changed to: $imageUrl (SharedKey: $sharedKey)")
    }

    // Log.d("PhotoImage", "Compose: id=$sharedKey, url=$imageUrl")

    val sharedModifier = if ((sharedTransitionScope != null) && (animatedVisibilityScope != null) && (sharedKey != null)) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                rememberSharedContentState(key = sharedKey),
                animatedVisibilityScope = animatedVisibilityScope,
                clipInOverlayDuringTransition = OverlayClip(shape)
            )
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(sharedModifier)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Progressive Image Loading with Fade Animation
        // This allows us to keep the placeholder visible while the high-res one loads.
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            onState = { state ->
                when (state) {
                    is AsyncImagePainter.State.Loading -> {
                        Log.d("PhotoImage", "Loading: $imageUrl")
                        onLoading?.invoke(true)
                    }
                    is AsyncImagePainter.State.Success -> {
                        Log.d("PhotoImage", "Success: $imageUrl")
                        onLoading?.invoke(false)
                        onSuccess?.invoke()
                    }
                    is AsyncImagePainter.State.Error -> {
                        onLoading?.invoke(false)
                        isError = true
                    }
                    else -> {}
                }
            },
            content = {
                val state = painter.state
                Crossfade(
                    targetState = state,
                    label = "photo_image_fade",
                    animationSpec = tween(durationMillis = 800),
                    modifier = Modifier.fillMaxSize()
                ) { currentState ->
                    when (currentState) {
                        is AsyncImagePainter.State.Success -> {
                            // Show the final Regular/High-res image (Level 3)
                            SubcomposeAsyncImageContent(
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            // While loading (or on error), show the placeholders
                            Box(Modifier.fillMaxSize()) {
                                // Level 1: Blur or Shimmer
                                if (blurBitmap != null) {
                                    Image(
                                        bitmap = blurBitmap,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.matchParentSize()
                                    )
                                } else {
                                    Box(
                                        Modifier
                                            .matchParentSize()
                                            .shimmer(shimmer)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    )
                                }

                                // Level 2: Thumbnail (if available)
                                placeholderUrl?.let {
                                    AsyncImage(
                                        model = it,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.matchParentSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )

        // 3. Error Overlay
        if (isError) {
            PhotoErrorOverlay(onRetry = onRetry)
        }
    }
}

@Composable
fun PhotoErrorOverlay(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .clickable { onRetry() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Tap to retry",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}
