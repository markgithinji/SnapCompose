package com.example.composegallery.feature.gallery.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.PhotoCollection
import com.example.composegallery.feature.gallery.ui.common.BottomLoadingIndicator
import com.example.composegallery.feature.gallery.ui.common.EmptyContentMessage
import com.example.composegallery.feature.gallery.ui.common.LoadMoreListError
import com.example.composegallery.feature.gallery.ui.common.calculateResponsiveColumnCount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileContent(
    name: String,
    bio: String?,
    location: String?,
    profileImageUrl: String,
    portfolioUrl: String?,
    instagramUsername: String?,
    totalPhotos: Int,
    totalLikes: Int,
    totalCollections: Int,
    userPhotos: LazyPagingItems<Photo>,
    userLikes: LazyPagingItems<Photo>,
    userCollections: LazyPagingItems<PhotoCollection>,
    onPhotoClick: (String) -> Unit,
    onCollectionClick: (String, String, Int) -> Unit,
    onStatsClick: () -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(UserTab.PHOTOS) }
    val retryKeys = remember { mutableStateMapOf<String, Int>() }
    val retryHandler: (String) -> Unit = { id ->
        retryKeys[id] = retryKeys.getOrDefault(id, 0) + 1
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "@${name}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onStatsClick() }) {
                        Icon(
                            Icons.Default.BarChart,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = stringResource(R.string.stats)
                        )
                    }
                }
            )
        }
    ) { padding ->
        val columnCount = calculateResponsiveColumnCount()

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(columnCount),
            contentPadding = PaddingValues(16.dp),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Profile Header
            item(span = StaggeredGridItemSpan.FullLine) {
                UserProfileHeader(
                    name = name,
                    profileImage = profileImageUrl,
                    bio = bio,
                    location = location,
                    portfolioUrl = portfolioUrl,
                    instagramUsername = instagramUsername
                )
            }

            // Stats Row as Tabs
            item(span = StaggeredGridItemSpan.FullLine) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItemTab(
                        count = totalPhotos.toString(),
                        label = stringResource(R.string.photos),
                        selected = selectedTab == UserTab.PHOTOS,
                        onClick = { selectedTab = UserTab.PHOTOS },
                        modifier = Modifier.weight(1f)
                    )
                    StatItemTab(
                        count = totalLikes.toString(),
                        label = stringResource(R.string.liked),
                        selected = selectedTab == UserTab.LIKES,
                        onClick = { selectedTab = UserTab.LIKES },
                        modifier = Modifier.weight(1f)
                    )
                    StatItemTab(
                        count = totalCollections.toString(),
                        label = stringResource(R.string.collections),
                        selected = selectedTab == UserTab.COLLECTIONS,
                        onClick = { selectedTab = UserTab.COLLECTIONS },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Photo Grid for selected tab
            when (selectedTab) {
                UserTab.PHOTOS -> renderPhotoItems(
                    photos = userPhotos,
                    retryKeys = retryKeys,
                    onRetry = retryHandler,
                    onPhotoClick = onPhotoClick
                )

                UserTab.LIKES -> renderPhotoItems(
                    photos = userLikes,
                    retryKeys = retryKeys,
                    onRetry = retryHandler,
                    onPhotoClick = onPhotoClick
                )

                UserTab.COLLECTIONS -> renderCollectionItems(
                    collections = userCollections,
                    retryKeys = retryKeys,
                    onRetry = retryHandler,
                    onCollectionClick = onCollectionClick
                )
            }
        }
    }
}

private fun LazyStaggeredGridScope.renderPhotoItems(
    photos: LazyPagingItems<Photo>,
    retryKeys: SnapshotStateMap<String, Int>,
    onRetry: (String) -> Unit,
    onPhotoClick: (String) -> Unit
) {
    val refreshState = photos.loadState.refresh

    items(
        count = photos.itemCount,
        key = { index ->
            val item = photos.peek(index)
            item?.id ?: index
        }
    ) { index ->
        val photo = photos[index] ?: return@items

        val retryKey = retryKeys[photo.id] ?: 0
        val url = if (retryKey > 0) "${photo.fullUrl}?retry=$retryKey" else photo.fullUrl

        ProfilePhotoCard(
            imageUrl = url,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(photo.width.toFloat() / photo.height)
                .clip(RoundedCornerShape(12.dp)),
            blurHash = photo.blurHash,
            onRetry = { onRetry(photo.id) },
            onClick = { onPhotoClick(photo.id) }
        )
    }

    if (refreshState is LoadState.Loading) {
        item(span = StaggeredGridItemSpan.FullLine) {
            BottomLoadingIndicator()
        }
    }

    if (refreshState is LoadState.Error) {
        item(span = StaggeredGridItemSpan.FullLine) {
            LoadMoreListError(
                message = refreshState.error.localizedMessage
                    ?: stringResource(R.string.error_loading_photos),
                onRetry = { photos.retry() }
            )
        }
    }

    if (refreshState is LoadState.NotLoading && photos.itemCount == 0) {
        item(span = StaggeredGridItemSpan.FullLine) {
            EmptyContentMessage(stringResource(R.string.no_photos_found))
        }
    }

    when (val appendState = photos.loadState.append) {
        is LoadState.Loading -> item(span = StaggeredGridItemSpan.FullLine) {
            BottomLoadingIndicator()
        }

        is LoadState.Error -> item(span = StaggeredGridItemSpan.FullLine) {
            LoadMoreListError(
                message = appendState.error.localizedMessage
                    ?: stringResource(R.string.error_loading_photos),
                onRetry = { photos.retry() }
            )
        }

        else -> {
            Unit // No-Op
        }
    }
}

private fun LazyStaggeredGridScope.renderCollectionItems(
    collections: LazyPagingItems<PhotoCollection>,
    retryKeys: SnapshotStateMap<String, Int>,
    onRetry: (String) -> Unit,
    onCollectionClick: (String, String, Int) -> Unit
) {
    val refreshState = collections.loadState.refresh

    items(
        count = collections.itemCount,
        key = { index ->
            val item = collections.peek(index)
            item?.id ?: index
        }
    ) { index ->
        val collection = collections[index] ?: return@items

        val retryKey = retryKeys[collection.id] ?: 0

        val imageUrl = if (retryKey > 0) {
            "${collection.coverPhoto?.regularUrl.orEmpty()}?retry=$retryKey"
        } else {
            collection.coverPhoto?.regularUrl.orEmpty()
        }

        CollectionGridItem(
            id = collection.id,
            coverPhoto = imageUrl,
            title = collection.title,
            totalPhotos = collection.totalPhotos,
            modifier = Modifier.fillMaxWidth(),
            blurHash = collection.coverPhoto?.blurHash,
            description = collection.description,
            onRetry = { onRetry(collection.id) },
            onCollectionClick = {
                onCollectionClick(
                    collection.id,
                    collection.title,
                    collection.totalPhotos
                )
            }
        )
    }

    if (refreshState is LoadState.Loading) {
        item(span = StaggeredGridItemSpan.FullLine) {
            BottomLoadingIndicator()
        }
    }

    if (refreshState is LoadState.Error) {
        item(span = StaggeredGridItemSpan.FullLine) {
            LoadMoreListError(
                message = refreshState.error.localizedMessage
                    ?: stringResource(R.string.error_loading_collections),
                onRetry = { collections.retry() }
            )
        }
    }

    if (refreshState is LoadState.NotLoading && collections.itemCount == 0) {
        item(span = StaggeredGridItemSpan.FullLine) {
            EmptyContentMessage(stringResource(R.string.no_collections_found))
        }
    }

    when (val appendState = collections.loadState.append) {
        is LoadState.Loading -> item(span = StaggeredGridItemSpan.FullLine) {
            BottomLoadingIndicator()
        }

        is LoadState.Error -> {
            item(span = StaggeredGridItemSpan.FullLine) {
                LoadMoreListError(
                    message = appendState.error.localizedMessage
                        ?: stringResource(R.string.error_loading_collections),
                    onRetry = { collections.retry() }
                )
            }
        }

        else -> {
            Unit // No-Op
        }
    }
}


@Composable
private fun StatItemTab(
    count: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(count, style = MaterialTheme.typography.headlineMedium)
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .height(2.dp)
                    .width(24.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    }
}

