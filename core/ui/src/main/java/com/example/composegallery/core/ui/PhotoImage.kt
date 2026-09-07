package com.example.composegallery.core.ui


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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.composegallery.core.util.BlurHashDecoder
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

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
    var isError by remember(imageUrl) { mutableStateOf(false) }
    
    // Track the successful resolution
    var lastSuccessfulPainter by remember(sharedKey) { mutableStateOf<Painter?>(null) }

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
        // Progressive Image Loading (Thumbnail -> High-Res)
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            onState = { state ->
                when (state) {
                    is AsyncImagePainter.State.Loading -> {
                        isError = false
                        onLoading?.invoke(true)
                    }
                    is AsyncImagePainter.State.Success -> {
                        lastSuccessfulPainter = state.painter
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
                
                // We keep the thumbnail visible during high-res upgrades to avoid flicker
                val displayPainter = (state as? AsyncImagePainter.State.Success)?.painter 
                    ?: (if (state is AsyncImagePainter.State.Loading) lastSuccessfulPainter else null)

                Box(Modifier.fillMaxSize()) {
                    if (displayPainter != null) {
                        Image(
                            painter = displayPainter,
                            contentDescription = contentDescription,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Initial loading (Blur or Shimmer)
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
