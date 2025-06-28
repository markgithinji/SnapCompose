package com.example.composegallery.feature.gallery.ui

import android.app.Activity
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.composegallery.feature.gallery.domain.model.Photo

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoGrid(
    photos: LazyPagingItems<Photo>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSearchClick: () -> Unit,
    onPhotoClick: (Photo) -> Unit
) {
    val retryKeys = remember { mutableStateMapOf<String, Int>() }
    val isGridClickable =
        photos.loadState.refresh !is LoadState.Loading &&
                photos.loadState.refresh !is LoadState.Error

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(calculateResponsiveColumnCount()),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            GalleryHeader(
                onSearchClick = onSearchClick,
                modifier = Modifier
                    .padding(
                        top = WindowInsets.statusBars.asPaddingValues()
                            .calculateTopPadding()// pushes header below status bar
                    ),
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }

        items(
            count = photos.itemCount,
            key = { index -> photos[index]?.id ?: index },
            span = { index ->
                if ((index + 1) % 5 == 0) {
                    StaggeredGridItemSpan.FullLine // Show a featured photo item in near full size at every 5th position
                } else {
                    StaggeredGridItemSpan.SingleLane
                }
            }
        ) { index ->
            val photo = photos[index]
            if (photo != null) {

                val retryKey = retryKeys[photo.id] ?: 0
                val url = if (retryKey > 0) "${photo.fullUrl}?retry=$retryKey" else photo.fullUrl

                PhotoCard(
                    imageUrl = url,
                    authorName = photo.authorName,
                    authorImageUrl = "${photo.authorProfileImageUrl}?retry=$retryKey",
                    onRetry = { retryKeys[photo.id] = retryKey + 1 },
                    blurHash = photo.blurHash,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(photo.width.toFloat() / photo.height)
                        .clip(RoundedCornerShape(12.dp)),
                    onClick = if (isGridClickable) {
                        { onPhotoClick(photo) }
                    } else null
                )
            }
        }

        when (val appendState = photos.loadState.append) {
            is LoadState.Loading -> item(span = StaggeredGridItemSpan.FullLine) {
                BottomLoadingIndicator()
            }

            is LoadState.Error -> item(span = StaggeredGridItemSpan.FullLine) {
                LoadMoreError(
                    message = appendState.error.localizedMessage ?: "Error loading more",
                    onRetry = { photos.retry() }
                )
            }

            else -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun calculateResponsiveColumnCount(): Int {
    val context = LocalContext.current
    val activity = context as? Activity ?: return 2 // default fallback

    val windowSizeClass = calculateWindowSizeClass(activity)

    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 2
        WindowWidthSizeClass.Medium -> 3
        WindowWidthSizeClass.Expanded -> 4
        else -> 2
    }
}

@Composable
fun BottomLoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        ProgressIndicator()
    }
}

@Composable
fun LoadMoreError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}