package com.example.composegallery.feature.gallery.ui.gallery

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.ui.common.EmptyContentMessage
import com.example.composegallery.feature.gallery.ui.common.PhotoCard
import com.example.composegallery.feature.gallery.ui.common.calculateResponsiveColumnCount
import com.example.composegallery.feature.gallery.ui.util.UiState
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
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 144.dp),
                        verticalItemSpacing = 12.dp,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(photos, key = { it.id }) { photo ->
                            PhotoCard(
                                imageUrl = photo.smallUrl,
                                authorName = photo.authorName,
                                authorImageUrl = photo.authorProfileImageMediumResUrl,
                                onRetry = { /* No-op for favorites */ },
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
                // Fallback for error state in favorites (unlikely but good for safety)
                EmptyContentMessage(
                    message = state.message
                )
            }
        }
    }
}
