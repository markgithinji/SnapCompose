package com.example.composegallery.feature.gallery.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.ui.common.BottomLoadingIndicator
import com.example.composegallery.feature.gallery.ui.common.LoadMoreListError
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailScreen(
    collectionId: String,
    collectionTitle: String,
    onBack: () -> Unit,
    onPhotoClick: (String) -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val photos = viewModel.collectionPhotos.collectAsLazyPagingItems()
    val retryKeys = remember { mutableStateMapOf<String, Int>() }

    LaunchedEffect(collectionId) {
        viewModel.loadCollectionPhotos(collectionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(collectionTitle, maxLines = 1, overflow = TextOverflow.Ellipsis) },
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
    ) { padding ->
        when (val refreshState = photos.loadState.refresh) {
            is LoadState.Loading -> {
                ProgressIndicator()
            }

            is LoadState.Error -> {
                LoadMoreListError(
                    message = refreshState.error.localizedMessage
                        ?: stringResource(R.string.error_failed_to_load_photos),
                    onRetry = { photos.retry() }
                )
            }

            else -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalItemSpacing = 12.dp,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = photos.itemCount,
                        key = { index ->
                            val item = photos.peek(index)
                            item?.id ?: index
                        }
                    ) { index ->
                        val photo = photos[index] ?: return@items
                        val retryKey = retryKeys[photo.id] ?: 0
                        val url =
                            if (retryKey > 0) "${photo.fullUrl}?retry=$retryKey" else photo.fullUrl

                        ProfilePhotoCard(
                            imageUrl = url,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(photo.width.toFloat() / photo.height)
                                .clip(RoundedCornerShape(12.dp)),
                            blurHash = photo.blurHash,
                            onRetry = { retryKeys[photo.id] = retryKey + 1 },
                            onClick = { onPhotoClick(photo.id) }
                        )
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

                        is LoadState.NotLoading -> {
                            Unit // No-Op
                        }
                    }
                }
            }
        }
    }
}