package com.example.composegallery.feature.gallery.ui.gallery

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.Topic
import com.example.composegallery.feature.gallery.ui.common.BottomLoadingIndicator
import com.example.composegallery.feature.gallery.ui.util.UiState
import com.example.composegallery.feature.gallery.ui.common.LoadMoreListError
import com.example.composegallery.feature.gallery.ui.common.PhotoCard
import com.example.composegallery.feature.gallery.ui.common.calculateResponsiveColumnCount
import com.valentinilk.shimmer.shimmer
import timber.log.Timber

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoGrid(
    photos: LazyPagingItems<Photo>,
    topicsState: UiState<List<Topic>>,
    selectedTopicId: String?,
    isSwitchingTopic: Boolean,
    onTopicSelected: (String?) -> Unit,
    onRetryTopics: () -> Unit,
    onPhotoClick: (Photo) -> Unit,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val retryKeys = remember { mutableStateMapOf<String, Int>() }
    val isGridClickable =
        photos.loadState.refresh !is LoadState.Loading &&
                photos.loadState.refresh !is LoadState.Error

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(calculateResponsiveColumnCount()),
        modifier = Modifier
            .fillMaxSize()
            .testTag("PhotoGrid"),
        contentPadding = PaddingValues(8.dp),
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
                onFavoritesClick = onFavoritesClick,
                onRetryTopics = onRetryTopics,
                modifier = Modifier.statusBarsPadding()
            )
        }

        val refreshState = photos.loadState.refresh
        if (isSwitchingTopic || (refreshState is LoadState.Loading && photos.itemCount == 0)) {
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
                key = { index ->
                    val item = photos.peek(index)
                    item?.id ?: index
                },
                span = { index ->
                    if ((index + 1) % 5 == 0) {
                        StaggeredGridItemSpan.FullLine
                    } else {
                        StaggeredGridItemSpan.SingleLane
                    }
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

            else -> {
                Unit // No-Op
            }
        }
    }
}
