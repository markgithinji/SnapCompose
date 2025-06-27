package com.example.composegallery.feature.gallery.ui

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun GalleryScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    viewModel: GalleryViewModel = hiltViewModel(),
    onSearchNavigate: () -> Unit
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
        Column {
            AnimatedContent(
                targetState = refreshState,
                transitionSpec = {
                    fadeIn(tween(500)) togetherWith fadeOut(tween(300))
                },
                label = "PhotoGridTransition"
            ) { state ->
                when (state) {
                    is LoadState.Loading -> {
                        // Show only if first load to prevent showing both main ProgressIndicator
                        // and PullToRefreshBox indicator simultaneously
                        if (!hasLoadedOnce.value) {
                            ProgressIndicator()
                        }
                    }

                    is LoadState.Error -> {
                        val error = state.error.localizedMessage
                        DataNotFoundBox(
                            message = error ?: "Unknown error",
                            onRetry = { photos.retry() }
                        )
                    }

                    else -> {
                        PhotoGrid(
                            photos = photos,
                            onSearchClick = onSearchNavigate,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator()
    }
}

@Composable
fun DataNotFoundBox(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Failed to load images:\n$message",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}