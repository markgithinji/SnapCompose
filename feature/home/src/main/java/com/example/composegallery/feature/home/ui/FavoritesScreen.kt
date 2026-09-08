package com.example.composegallery.feature.home.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composegallery.core.ui.R
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.ui.EmptyContentMessage
import com.example.composegallery.core.ui.PhotoCard
import com.example.composegallery.core.ui.calculateResponsiveColumnCount
import com.example.composegallery.core.common.UiState
import com.valentinilk.shimmer.shimmer
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FavoritesScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    onPhotoClick: (Photo) -> Unit,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val favoritesState by viewModel.favoritesState.collectAsStateWithLifecycle()
    val gridState = viewModel.favoritesGridState
    val retryKeys = remember { mutableStateMapOf<String, Int>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.favorites),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 16.dp)
        )

        when (val state = favoritesState) {
            is UiState.Loading -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(calculateResponsiveColumnCount()),
                    state = gridState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 144.dp),
                    verticalItemSpacing = 12.dp,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(10) { index ->
                        val aspectRatio = if (index % 2 == 0) 0.7f else 1.3f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(aspectRatio)
                                .clip(RoundedCornerShape(12.dp))
                                .shimmer()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }
            }

            is UiState.Content -> {
                val photos = state.data
                if (photos.isEmpty()) {
                    Box(modifier = Modifier.weight(1f)) {
                        EmptyContentMessage(
                            message = stringResource(R.string.no_favorites_yet)
                        )
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(calculateResponsiveColumnCount()),
                        state = gridState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 144.dp),
                        verticalItemSpacing = 12.dp,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(photos, key = { it.id }) { photo ->
                            val retryKey = retryKeys[photo.id] ?: 0
                            val url = if (retryKey > 0) "${photo.smallUrl}?retry=$retryKey" else photo.smallUrl

                            PhotoCard(
                                imageUrl = url,
                                authorName = photo.authorName,
                                authorImageUrl = photo.authorProfileImageMediumResUrl,
                                onRetry = { retryKeys[photo.id] = retryKey + 1 },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                photoId = photo.id,
                                origin = "favorites",
                                aspectRatio = photo.width.toFloat() / photo.height,
                                blurHash = photo.blurHash,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onPhotoClick(photo) }
                            )
                        }
                    }
                }
            }
            
            is UiState.Error -> {
                EmptyContentMessage(
                    message = state.message
                )
            }
        }
    }
}
