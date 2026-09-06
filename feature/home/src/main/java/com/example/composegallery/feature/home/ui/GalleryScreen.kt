package com.example.composegallery.feature.home.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.composegallery.core.ui.R
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.ui.ProgressIndicator
import com.example.composegallery.core.common.UiState
import com.example.composegallery.core.model.Topic

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun GalleryScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    viewModel: GalleryViewModel = hiltViewModel(),
    onSearchNavigate: () -> Unit,
    onPhotoClick: (Photo) -> Unit
) {
    val photos = viewModel.pagedPhotos.collectAsLazyPagingItems()
    val topicsState by viewModel.topicsState.collectAsStateWithLifecycle()
    val selectedTopicId by viewModel.selectedTopicId.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullToRefreshState()
    val gridState = viewModel.gridState
    var isManualRefreshing by remember { mutableStateOf(false) }
    val refreshState = photos.loadState.refresh

    var isSwitchingTopic by rememberSaveable(selectedTopicId) { 
        mutableStateOf(photos.itemCount == 0) 
    }
    var hasSeenLoadingForTab by rememberSaveable(selectedTopicId) { 
        mutableStateOf(false) 
    }

    LaunchedEffect(refreshState, photos.itemCount) {
        if (refreshState !is LoadState.Loading) {
            isManualRefreshing = false
        }

        if (refreshState is LoadState.Loading) {
            hasSeenLoadingForTab = true
        }
        
        val canShowContent = hasSeenLoadingForTab || photos.itemCount > 0
        if (isSwitchingTopic && canShowContent && refreshState !is LoadState.Loading) {
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
            gridState = gridState,
            topicsState = topicsState,
            selectedTopicId = selectedTopicId,
            isSwitchingTopic = isSwitchingTopic,
            onTopicSelected = { viewModel.selectTopic(it) },
            onRetryTopics = { viewModel.fetchTopics() },
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
    gridState: LazyStaggeredGridState,
    topicsState: UiState<List<Topic>>,
    selectedTopicId: String?,
    isSwitchingTopic: Boolean,
    onTopicSelected: (String?) -> Unit,
    onRetryTopics: () -> Unit,
    onPhotoClick: (Photo) -> Unit,
    onRetry: () -> Unit,
    onSearchClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope
) {
    PhotoGrid(
        photos = photos,
        gridState = gridState,
        topicsState = topicsState,
        selectedTopicId = selectedTopicId,
        isSwitchingTopic = isSwitchingTopic,
        onTopicSelected = onTopicSelected,
        onRetryTopics = onRetryTopics,
        onPhotoClick = onPhotoClick,
        onRetry = onRetry,
        onSearchClick = onSearchClick,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}
