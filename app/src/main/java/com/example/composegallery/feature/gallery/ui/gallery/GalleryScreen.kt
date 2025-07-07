package com.example.composegallery.feature.gallery.ui.gallery

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.ui.common.InfoMessageScreen
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator
import com.example.composegallery.feature.gallery.ui.common.RetryButton


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun GalleryScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    viewModel: GalleryViewModel = hiltViewModel(),
    onSearchNavigate: () -> Unit,
    onPhotoClick: (String) -> Unit
) {
    val photos = viewModel.pagedPhotos.collectAsLazyPagingItems()
    val pullRefreshState = rememberPullToRefreshState()
    val hasLoadedOnce = remember { mutableStateOf(false) }
    val refreshState = photos.loadState.refresh
    // Only true for pull-to-refresh after first successful load
    val isRefreshing = hasLoadedOnce.value && refreshState is LoadState.Loading

    LaunchedEffect(refreshState) {
        if (refreshState is LoadState.NotLoading) {
            hasLoadedOnce.value = true
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { photos.refresh() },
        state = pullRefreshState,
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        indicator = {
            val progress = pullRefreshState.distanceFraction.coerceIn(0f, 1f)
            if (progress > 0f || isRefreshing) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .graphicsLayer {
                            translationY = progress * 100f
                            alpha = progress
                        }
                        .size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ProgressIndicator()
                }
            }
        }
    ) {
        PhotoGridContent(
            photos = photos,
            loadState = refreshState,
            hasLoadedOnce = hasLoadedOnce.value,
            onPhotoClick = onPhotoClick,
            onRetry = { photos.retry() },
            onSearchClick = onSearchNavigate,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PhotoGridContent(
    photos: LazyPagingItems<Photo>,
    loadState: LoadState,
    hasLoadedOnce: Boolean,
    onPhotoClick: (String) -> Unit,
    onRetry: () -> Unit,
    onSearchClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope
) {

    Column {
        AnimatedContent(
            targetState = loadState,
            transitionSpec = {
                fadeIn(tween(500)) togetherWith fadeOut(tween(300))
            },
            label = "PhotoGridTransition"
        ) { state ->
            when (state) {
                is LoadState.Loading -> {
                    // Show only if first load to prevent showing both main ProgressIndicator
                    // and PullToRefreshBox indicator simultaneously
                    if (!hasLoadedOnce) {
                        ProgressIndicator()
                    }
                }

                is LoadState.Error -> {
                    val reason = state.error.localizedMessage?.let {
                        stringResource(R.string.error_reason_prefix, it)
                    } ?: stringResource(R.string.unknown_error)

                    InfoMessageScreen(
                        title = stringResource(R.string.error_load_photos),
                        subtitle = reason,
                        imageRes = R.drawable.error_icon,
                        titleColor = MaterialTheme.colorScheme.error
                    ) {
                        RetryButton(onClick = onRetry)
                    }
                }

                else -> {
                    PhotoGrid(
                        photos = photos,
                        onPhotoClick = { onPhotoClick(it.id) },
                        onSearchClick = onSearchClick,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }
    }
}
