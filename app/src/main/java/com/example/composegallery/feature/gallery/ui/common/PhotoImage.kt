package com.example.composegallery.feature.gallery.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.composegallery.feature.gallery.ui.util.BlurHashDecoder
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun PhotoImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    blurHash: String? = null,
    onRetry: () -> Unit
) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    // Decode the blur hash only once per blurHash value
    val blurBitmap: ImageBitmap? = remember(blurHash) {
        blurHash?.let { BlurHashDecoder.decode(it, 20, 12)?.asImageBitmap() }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
    ) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        ) {
            AnimatedContent(
                targetState = painter.state,
                transitionSpec = {
                    fadeIn(tween(500)) togetherWith fadeOut(tween(350))
                },
                label = "AsyncImageStateTransition"
            ) { state ->
                when (state) {
                    is AsyncImagePainter.State.Loading,
                    is AsyncImagePainter.State.Empty -> {
                        if (blurBitmap != null) {
                            Image(
                                bitmap = blurBitmap,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                        } else { // fallback to shimmer
                            Box(
                                Modifier
                                    .matchParentSize()
                                    .shimmer(shimmer)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }

                    is AsyncImagePainter.State.Success -> {
                        SubcomposeAsyncImageContent(
                            modifier = Modifier.matchParentSize()
                        )
                    }

                    is AsyncImagePainter.State.Error -> {
                        PhotoErrorOverlay(onRetry = onRetry)
                    }
                }
            }
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
