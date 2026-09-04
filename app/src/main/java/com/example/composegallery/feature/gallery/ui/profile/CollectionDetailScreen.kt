package com.example.composegallery.feature.gallery.ui.profile

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.compose.itemContentType
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.ui.common.BottomLoadingIndicator
import com.example.composegallery.feature.gallery.ui.common.EmptyContentMessage
import com.example.composegallery.feature.gallery.ui.common.InfoMessageScreen
import com.example.composegallery.feature.gallery.ui.common.LoadMoreListError
import com.example.composegallery.feature.gallery.ui.common.PhotoImage
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator
import com.example.composegallery.feature.gallery.ui.common.RetryButton
import com.example.composegallery.feature.gallery.ui.common.SharedTransitionKeys
import com.example.composegallery.feature.gallery.ui.common.calculateResponsiveColumnCount
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CollectionDetailScreen(
    collectionId: String,
    collectionTitle: String,
    totalPhotos: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    onBack: () -> Unit,
    onPhotoClick: (Photo) -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val photos = viewModel.collectionPhotos.collectAsLazyPagingItems()
    val retryKeys = remember { mutableStateMapOf<String, Int>() }
    val gridState = rememberLazyStaggeredGridState()

    LaunchedEffect(collectionId) {
        viewModel.setCollectionId(collectionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = collectionTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "•", // Large dot
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.total_photos_format, totalPhotos),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_content_description)
                        )
                    }
                }
            )
        }
    )
    { padding ->
        when (val refreshState = photos.loadState.refresh) {
            is LoadState.Loading -> {
                ProgressIndicator(modifier = Modifier.fillMaxSize())
            }

            is LoadState.Error -> {
                InfoMessageScreen(
                    imageRes = R.drawable.error_icon,
                    title = stringResource(R.string.error_failed_to_load_photos),
                    subtitle = stringResource(
                        R.string.reason,
                        refreshState.error.localizedMessage ?: stringResource(R.string.unknown_error)
                    ),
                    titleColor = MaterialTheme.colorScheme.error
                ) {
                    RetryButton(onClick = { photos.retry() })
                }
            }

            else -> {
                if (photos.itemCount == 0 && refreshState is LoadState.NotLoading) {
                    EmptyContentMessage(
                        message = stringResource(R.string.no_photos_found_in_collection),
                        modifier = Modifier.padding(padding)
                    )
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(calculateResponsiveColumnCount()),
                        state = gridState,
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 32.dp),
                        verticalItemSpacing = 12.dp,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            count = photos.itemCount,
                            key = photos.itemKey { it.id },
                            contentType = photos.itemContentType { "photo" }
                        ) { index ->
                            val photo = photos[index]
                            if (photo != null) {
                                val retryKey = retryKeys[photo.id] ?: 0
                                val url = if (retryKey > 0) "${photo.smallUrl}?retry=$retryKey" else photo.smallUrl

                                ProfilePhotoCard(
                                    imageUrl = url,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(photo.width.toFloat() / photo.height)
                                        .clip(RoundedCornerShape(12.dp)),
                                    blurHash = photo.blurHash,
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    photoId = photo.id,
                                    origin = "collection_$collectionId",
                                    onRetry = { retryKeys[photo.id] = retryKey + 1 },
                                    onClick = { onPhotoClick(photo) }
                                )
                            } else {
                                // Placeholder
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .shimmer()
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                            }
                        }

                        // Pagination footer
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
            }
        }
    }
}
