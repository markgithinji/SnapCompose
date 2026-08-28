package com.example.composegallery.feature.gallery.ui.gallery

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.Topic
import com.example.composegallery.feature.gallery.ui.common.InfoMessageScreen
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator
import com.example.composegallery.feature.gallery.ui.common.RetryButton
import com.example.composegallery.feature.gallery.ui.util.UiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun GalleryScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    viewModel: GalleryViewModel = hiltViewModel(),
    onSearchNavigate: () -> Unit,
    onFavoritesNavigate: () -> Unit,
    onPhotoClick: (Photo) -> Unit
) {
    val photos = viewModel.pagedPhotos.collectAsLazyPagingItems()
    val topicsState by viewModel.topicsState.collectAsStateWithLifecycle()
    val selectedTopicId by viewModel.selectedTopicId.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullToRefreshState()
    var isManualRefreshing by remember { mutableStateOf(false) }
    val refreshState = photos.loadState.refresh

    // Force a "switching" state when the tab changes so we see shimmers immediately.
    // For the Editorial tab (null), we allow showing cached data immediately to avoid flicker.
    var isSwitchingTopic by remember(selectedTopicId) { mutableStateOf(selectedTopicId != null) }
    var hasSeenLoadingForTab by remember(selectedTopicId) { mutableStateOf(false) }

    LaunchedEffect(refreshState) {
        if (refreshState !is LoadState.Loading) {
            isManualRefreshing = false
        }

        if (refreshState is LoadState.Loading) {
            hasSeenLoadingForTab = true
        }
        
        // Hide shimmers only after we've seen a loading cycle and it finished,
        // or if we already have data and the tab didn't trigger a new load
        if (isSwitchingTopic && hasSeenLoadingForTab && refreshState is LoadState.NotLoading) {
            isSwitchingTopic = false
        }
    }

    PullToRefreshBox(
        isRefreshing = isManualRefreshing,
        onRefresh = {
            isManualRefreshing = true
            photos.refresh()
        },
        state = pullRefreshState,
        modifier = Modifier.fillMaxSize(),
        indicator = {
            val progress = pullRefreshState.distanceFraction.coerceIn(0f, 1f)
            if (progress > 0f || isManualRefreshing) {
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
                    ProgressIndicator(indicatorSize = 20.dp)
                }
            }
        }
    ) {
        PhotoGridContent(
            photos = photos,
            topicsState = topicsState,
            selectedTopicId = selectedTopicId,
            isSwitchingTopic = isSwitchingTopic,
            onTopicSelected = { viewModel.selectTopic(it) },
            onRetryTopics = { viewModel.fetchTopics() },
            loadState = refreshState,
            onPhotoClick = onPhotoClick,
            onRetry = { photos.retry() },
            onSearchClick = onSearchNavigate,
            onFavoritesClick = onFavoritesNavigate,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PhotoGridContent(
    photos: LazyPagingItems<Photo>,
    topicsState: UiState<List<Topic>>,
    selectedTopicId: String?,
    isSwitchingTopic: Boolean,
    onTopicSelected: (String?) -> Unit,
    onRetryTopics: () -> Unit,
    loadState: LoadState,
    onPhotoClick: (Photo) -> Unit,
    onRetry: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (loadState is LoadState.Error && photos.itemCount == 0) {
            val reason = loadState.error.localizedMessage?.let {
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
        } else {
            PhotoGrid(
                photos = photos,
                topicsState = topicsState,
                selectedTopicId = selectedTopicId,
                isSwitchingTopic = isSwitchingTopic,
                onTopicSelected = onTopicSelected,
                onRetryTopics = onRetryTopics,
                onPhotoClick = onPhotoClick,
                onSearchClick = onSearchClick,
                onFavoritesClick = onFavoritesClick,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    }
}
