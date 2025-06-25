package com.example.composegallery.feature.gallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun PhotoCard(
    imageUrl: String,
    aspectRatio: Float,
    authorName: String,
    authorImageUrl: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        PhotoImage(
            imageUrl = imageUrl,
            aspectRatio = aspectRatio,
            contentDescription = authorName,
            onRetry = onRetry
        )

        Spacer(modifier = Modifier.height(4.dp))

        AuthorInfoRow(authorName = authorName, authorImageUrl = authorImageUrl)
    }
}

@Composable
fun PhotoImage(
    imageUrl: String,
    aspectRatio: Float,
    contentDescription: String,
    onRetry: () -> Unit
) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(12.dp))
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading,
            is AsyncImagePainter.State.Empty -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .shimmer(shimmer)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()

            is AsyncImagePainter.State.Error -> PhotoErrorOverlay(onRetry)
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
        Text("⚠", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tap to retry", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun AuthorInfoRow(authorName: String, authorImageUrl: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 4.dp)
    ) {
        AsyncImage(
            model = authorImageUrl,
            contentDescription = "$authorName's profile picture",
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = authorName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}