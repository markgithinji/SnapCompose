package com.example.composegallery.feature.gallery.ui.profile

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.PhotoCollection
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import com.example.composegallery.feature.gallery.ui.common.BottomLoadingIndicator
import com.example.composegallery.feature.gallery.ui.common.LoadMoreListError
import com.example.composegallery.feature.gallery.ui.common.PhotoImage
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator
import com.example.composegallery.feature.gallery.ui.common.calculateResponsiveColumnCount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileContent(
    user: UnsplashUser,
    onPhotoClick: (String) -> Unit,
    onCollectionClick: (String, String) -> Unit,
    onStatsClick: () -> Unit,
    onBack: () -> Unit,
    userPhotos: LazyPagingItems<Photo>,
    userLikes: LazyPagingItems<Photo>,
    userCollections: LazyPagingItems<PhotoCollection>
) {
    var selectedTab by rememberSaveable { mutableStateOf(UserTab.PHOTOS) }
    val retryKeys = remember { mutableStateMapOf<String, Int>() }
    val retryHandler: (String) -> Unit = { id ->
        retryKeys[id] = retryKeys.getOrDefault(id, 0) + 1
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "@${user.username}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onStatsClick() }) {
                        Icon(
                            Icons.Default.BarChart,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Stats"
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
                UserProfileHeader(user = user)
            }

            // Stats Row as Tabs
            item(span = StaggeredGridItemSpan.FullLine) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItemTab(
                        count = user.totalPhotos.toString(),
                        label = "Photos",
                        selected = selectedTab == UserTab.PHOTOS,
                        onClick = { selectedTab = UserTab.PHOTOS }
                    )
                    StatItemTab(
                        count = user.totalLikes.toString(),
                        label = "Liked",
                        selected = selectedTab == UserTab.LIKES,
                        onClick = { selectedTab = UserTab.LIKES }
                    )
                    StatItemTab(
                        count = user.totalCollections.toString(),
                        label = "Collections",
                        selected = selectedTab == UserTab.COLLECTIONS,
                        onClick = { selectedTab = UserTab.COLLECTIONS }
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
                    onCollectionClick = { collection ->
                        onCollectionClick(collection.id, collection.title)
                    }
                )
            }
        }
    }
}

fun LazyStaggeredGridScope.renderPhotoItems(
    photos: LazyPagingItems<Photo>,
    retryKeys: SnapshotStateMap<String, Int>,
    onRetry: (String) -> Unit,
    onPhotoClick: (String) -> Unit
) {
    val isGridClickable =
        photos.loadState.refresh !is LoadState.Loading &&
                photos.loadState.refresh !is LoadState.Error

    items(
        count = photos.itemCount,
        key = { index -> photos[index]?.id ?: index }
    ) { index ->
        val photo = photos[index] ?: return@items

        val retryKey = retryKeys[photo.id] ?: 0
        val url = if (retryKey > 0) "${photo.fullUrl}?retry=$retryKey" else photo.fullUrl

        ProfilePhotoCard(
            imageUrl = url,
            onRetry = { onRetry(photo.id) },
            blurHash = photo.blurHash,
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth()
                .aspectRatio(photo.width.toFloat() / photo.height)
                .clip(RoundedCornerShape(12.dp)),
            onClick = if (isGridClickable) {
                { onPhotoClick(photo.id) }
            } else null
        )
    }

    when (val appendState = photos.loadState.append) {
        is LoadState.Loading -> item(span = StaggeredGridItemSpan.FullLine) {
            BottomLoadingIndicator()
        }

        is LoadState.Error -> item(span = StaggeredGridItemSpan.FullLine) {
            LoadMoreListError(
                message = appendState.error.localizedMessage ?: "Error loading more",
                onRetry = { photos.retry() }
            )
        }

        else -> Unit
    }
}

fun LazyStaggeredGridScope.renderCollectionItems(
    collections: LazyPagingItems<PhotoCollection>,
    onCollectionClick: (PhotoCollection) -> Unit = {}
) {
    // Render collection items
    items(count = collections.itemCount) { index ->
        val collection = collections[index] ?: return@items

        CollectionGridItem(
            collection = collection,
            onCollectionClick = { onCollectionClick(collection) },
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth()
        )
    }

    when (val appendState = collections.loadState.append) {
        is LoadState.Loading -> {
            item(span = StaggeredGridItemSpan.FullLine) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ProgressIndicator()
                }
            }
        }

        is LoadState.Error -> {
            item(span = StaggeredGridItemSpan.FullLine) {
                LoadMoreListError(
                    message = appendState.error.localizedMessage ?: "Error loading more",
                    onRetry = { collections.retry() }
                )
            }
        }

        is LoadState.NotLoading -> {
            if (collections.itemCount == 0) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        "No collections found.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun StatItemTab(
    count: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
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

@Composable
fun ProfilePhotoCard(
    imageUrl: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    blurHash: String? = null,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = modifier
        .clickable(enabled = onClick != null) { onClick?.invoke() }
        .padding(8.dp)

    PhotoImage(
        imageUrl = imageUrl,
        contentDescription = "User photo",
        blurHash = blurHash,
        onRetry = onRetry,
        modifier = clickableModifier
    )
}