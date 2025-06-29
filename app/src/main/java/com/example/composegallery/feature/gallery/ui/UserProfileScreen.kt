package com.example.composegallery.feature.gallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.composegallery.feature.gallery.domain.model.Collection
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import com.example.composegallery.feature.gallery.domain.model.UserStatistics

@Composable
fun UserProfileScreen(
    username: String,
    onPhotoClick: (String) -> Unit,
    onCollectionClick: (String, String) -> Unit,
    onBack: () -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val userProfileState by viewModel.userProfileState.collectAsStateWithLifecycle()
    val userStatsState by viewModel.userStatisticsState.collectAsStateWithLifecycle()
    val photos = viewModel.pagedUserPhotos.collectAsLazyPagingItems()
    val userLikes = viewModel.userLikedPhotos.collectAsLazyPagingItems()
    val collections = viewModel.userCollectionsState.collectAsLazyPagingItems()

    var showStatsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(username) {
        viewModel.loadUserProfile(username)
        viewModel.loadUserPhotos(username)
        viewModel.loadUserCollections(username)
        viewModel.loadUserLikedPhotos(username)
        viewModel.loadUserStatistics(username)
    }

    when (userProfileState) {
        is UiState.Loading -> {
            ProgressIndicator()
        }

        is UiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${(userProfileState as UiState.Error).message}")
            }
        }

        is UiState.Content -> {
            val user = (userProfileState as UiState.Content<UnsplashUser>).data
            UserProfileContent(
                user = user,
                onPhotoClick = onPhotoClick,
                onCollectionClick = onCollectionClick,
                onStatsClick = { showStatsDialog = true },
                onBack = onBack,
                userPhotos = photos,
                userLikes = userLikes,
                userCollections = collections
            )
        }
    }

    // Show the stats dialog if requested
    if (showStatsDialog) {
        if (userStatsState is UiState.Content) {
            val stats = (userStatsState as UiState.Content<UserStatistics>).data
            AlertDialog(
                onDismissRequest = { showStatsDialog = false },
                title = { Text("User Statistics") },
                text = {
                    UserStatsChart(
                        stats = stats,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showStatsDialog = false }) {
                        Text("Close")
                    }
                }
            )
        } else if (userStatsState is UiState.Loading) {
            AlertDialog(
                onDismissRequest = { showStatsDialog = false },
                title = { Text("Loading Stats...") },
                text = { ProgressIndicator() },
                confirmButton = {
                    TextButton(onClick = { showStatsDialog = false }) {
                        Text("Close")
                    }
                }
            )
        } else if (userStatsState is UiState.Error) {
            AlertDialog(
                onDismissRequest = { showStatsDialog = false },
                title = { Text("Error Loading Stats") },
                text = { Text((userStatsState as UiState.Error).message) },
                confirmButton = {
                    TextButton(onClick = { showStatsDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileContent(
    user: UnsplashUser,
    onPhotoClick: (String) -> Unit,
    onCollectionClick: (String, String) -> Unit,
    onStatsClick: (String) -> Unit,
    onBack: () -> Unit,
    userPhotos: LazyPagingItems<Photo>,
    userLikes: LazyPagingItems<Photo>,
    userCollections: LazyPagingItems<Collection>
) {
    var selectedTab by remember { mutableStateOf(UserTab.PHOTOS) }
    val retryKeys = remember { mutableStateMapOf<String, Int>() }

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
                    IconButton(onClick = { onStatsClick(user.username) }) {
                        Icon(Icons.Default.BarChart, contentDescription = "Stats")
                    }
                }
            )
        }
    ) { padding ->

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Profile Header
            item(span = StaggeredGridItemSpan.FullLine) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = user.profileImageLarge,
                        contentDescription = "${user.name}'s profile picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(user.name, style = MaterialTheme.typography.titleLarge)
                        user.location?.let { Text(it) }
                        user.instagramUsername?.let {
                            Text("@$it", color = MaterialTheme.colorScheme.primary)
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier.height(32.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6E6E))
                        ) {
                            Text("Follow", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
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
                    onRetry = { id -> retryKeys[id] = (retryKeys[id] ?: 0) + 1 },
                    onPhotoClick = onPhotoClick
                )

                UserTab.LIKES -> renderPhotoItems(
                    photos = userLikes,
                    retryKeys = retryKeys,
                    onRetry = { id -> retryKeys[id] = (retryKeys[id] ?: 0) + 1 },
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
            LoadMoreError(
                message = appendState.error.localizedMessage ?: "Error loading more",
                onRetry = { photos.retry() }
            )
        }

        else -> Unit
    }
}

fun LazyStaggeredGridScope.renderCollectionItems(
    collections: LazyPagingItems<Collection>,
    onCollectionClick: (Collection) -> Unit = {}
) {
    items(count = collections.itemCount) { index ->
        val collection = collections[index] ?: return@items

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onCollectionClick(collection) }
        ) {
            AsyncImage(
                model = collection.coverPhoto?.regularUrl,
                contentDescription = collection.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.4f)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = collection.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            collection.description?.let {
                if (it.isNotBlank()) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${collection.totalPhotos} photos",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }

    if (collections.loadState.refresh is LoadState.Loading) {
        items(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }

    if (collections.loadState.append is LoadState.Loading) {
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

    // Error handling
    if (collections.loadState.refresh is LoadState.Error) {
        val error = collections.loadState.refresh as LoadState.Error
        item(span = StaggeredGridItemSpan.FullLine) {
            Text(
                text = "Error: ${error.error.localizedMessage ?: "Unknown error"}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
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
        Text(count, style = MaterialTheme.typography.titleMedium)
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
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun ProfilePhotoCard(
    imageUrl: String,
    onRetry: () -> Unit,
    blurHash: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = Modifier
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        .padding(8.dp)

    Box(modifier = clickableModifier) {
        PhotoImage(
            imageUrl = imageUrl,
            contentDescription = "User photo",
            blurHash = blurHash,
            onRetry = onRetry,
            modifier = modifier
        )
    }
}