package com.example.composegallery.feature.home.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.compose.itemContentType
import com.example.composegallery.core.ui.R
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.ui.BottomLoadingIndicator
import com.example.composegallery.core.ui.InfoMessageScreen
import com.example.composegallery.core.ui.LoadMoreListError
import com.example.composegallery.core.ui.PhotoCard
import com.example.composegallery.core.ui.RetryButton
import com.example.composegallery.core.ui.calculateResponsiveColumnCount
import com.example.composegallery.core.util.UiState
import com.example.composegallery.core.model.Topic
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoGrid(
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
    val retryKeys = remember { mutableStateMapOf<String, Int>() }
    val isGridClickable =
        photos.loadState.refresh !is LoadState.Loading &&
                photos.loadState.refresh !is LoadState.Error

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(calculateResponsiveColumnCount()),
        state = gridState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("PhotoGrid"),
        contentPadding = PaddingValues(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 144.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            GalleryHeader(
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                topicsState = topicsState,
                selectedTopicId = selectedTopicId,
                onTopicSelected = onTopicSelected,
                onSearchClick = onSearchClick,
                onRetryTopics = onRetryTopics,
                modifier = Modifier.statusBarsPadding()
            )
        }

        val refreshState = photos.loadState.refresh
        
        // We show an error area if:
        // 1. We are on the Editorial tab and have no cached data.
        // 2. We are on a specific Topic tab and the load failed (regardless of itemCount, 
        //    as any items seen here would be leftover from a previous tab).
        val isEditorial = selectedTopicId == null
        val isError = refreshState is LoadState.Error
        val hasNoItems = photos.itemCount == 0

        if (isError && (hasNoItems || !isEditorial)) {
            item(span = StaggeredGridItemSpan.FullLine) {
                val reason = (refreshState as LoadState.Error).error.localizedMessage?.let {
                    stringResource(R.string.error_reason_prefix, it)
                } ?: stringResource(R.string.unknown_error)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp, bottom = 64.dp)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    InfoMessageScreen(
                        title = stringResource(R.string.error_load_photos),
                        subtitle = reason,
                        imageRes = R.drawable.error_icon,
                        titleColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth() 
                    ) {
                        RetryButton(onClick = onRetry)
                    }
                }
            }
        } else if (isSwitchingTopic || (refreshState is LoadState.Loading && photos.itemCount == 0)) {
            items(10) { index ->
                val aspectRatio = if (index % 2 == 0) 0.7f else 1.3f
                Box(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmer()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        } else {
            items(
                count = photos.itemCount,
                key = photos.itemKey { "topic_${selectedTopicId ?: "editorial"}_${it.id}" },
                contentType = photos.itemContentType { "photo" },
                span = { index ->
                    val photo = photos.peek(index)
                    val isFullLine = photo?.let { it.id.hashCode() % 11 == 0 } ?: false
                    if (isFullLine) StaggeredGridItemSpan.FullLine else StaggeredGridItemSpan.SingleLane
                }
            ) { index ->
                val photo = photos[index]
                if (photo != null) {
                    val retryKey = retryKeys[photo.id] ?: 0
                    val url = if (retryKey > 0) "${photo.smallUrl}?retry=$retryKey" else photo.smallUrl

                    PhotoCard(
                        imageUrl = url,
                        authorName = photo.authorName,
                        authorImageUrl = "${photo.authorProfileImageMediumResUrl}?retry=$retryKey",
                        onRetry = { retryKeys[photo.id] = retryKey + 1 },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        photoId = photo.id,
                        origin = "gallery",
                        aspectRatio = photo.width.toFloat() / photo.height,
                        modifier = Modifier
                            .animateItem(
                                fadeInSpec = spring(stiffness = Spring.StiffnessLow),
                                fadeOutSpec = spring(stiffness = Spring.StiffnessLow),
                                placementSpec = spring(stiffness = Spring.StiffnessMedium)
                            )
                            .fillMaxWidth()
                            .testTag("PhotoItem_${photo.id}"),
                        blurHash = photo.blurHash,
                        onClick = takeIf { isGridClickable }?.let { { onPhotoClick(photo) } }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shimmer()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
        }

        when (val appendState = photos.loadState.append) {
            is LoadState.Loading -> item(span = StaggeredGridItemSpan.FullLine) {
                BottomLoadingIndicator()
            }

            is LoadState.Error -> item(span = StaggeredGridItemSpan.FullLine) {
                LoadMoreListError(
                    message = appendState.error.localizedMessage
                        ?: stringResource(R.string.error_loading_more),
                    onRetry = { photos.retry() }
                )
            }

            else -> {}
        }
    }
}
