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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.composegallery.feature.gallery.domain.model.Collection
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.PhotoLocation
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import kotlin.random.Random

@Composable
fun UserProfileScreen(
    username: String,
    onBack: () -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.userProfileState.collectAsStateWithLifecycle()
    val userPhotosState by viewModel.userPhotos.collectAsStateWithLifecycle()

    LaunchedEffect(username) {
        viewModel.loadUserProfile(username)
        viewModel.loadUserPhotos(username)
    }

    when (uiState) {
        is UiState.Loading -> {
            ProgressIndicator()
        }

        is UiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${(uiState as UiState.Error).message}")
            }
        }

        is UiState.Content -> {
            val user = (uiState as UiState.Content<UnsplashUser>).data
            UserProfileContent(
                user = user,
                onBack = onBack,
                userPhotosState = userPhotosState,
                userLikesState = UiState.Content(
                    listOf(
                        Photo( // fake photo
                            id = "like1",
                            width = 1080,
                            height = 720,
                            thumbUrl = "https://source.unsplash.com/random/300x300?sig=1",
                            regularUrl = "https://source.unsplash.com/random/600x400?sig=1",
                            fullUrl = "https://source.unsplash.com/random/1200x800?sig=1",
                            authorName = "Jane Doe",
                            authorProfileImageUrl = "",
                            authorProfileImageMediumResUrl = "",
                            authorProfileImageHighResUrl = "",
                            username = "janedoe",
                            location = PhotoLocation(city = "Nairobi", country = "Kenya"),
                            blurHash = null,
                            description = "Liked photo",
                            createdAt = "2023-01-01T00:00:00Z",
                            exif = null
                        )
                    )
                ),
                userCollectionsState = UiState.Content(
                    listOf(
                        Collection( // fake collection
                            id = "collection1",
                            title = "Urban Shots",
                            description = "A curated collection of city photos",
                            totalPhotos = 10,
                            coverPhoto = null,
                            authorName = "Jane Doe"
                        )
                    )
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileContent(
    user: UnsplashUser,
    userPhotosState: UiState<List<Photo>>,
    userLikesState: UiState<List<Photo>>,
    userCollectionsState: UiState<List<Collection>>,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(UserTab.PHOTOS) }

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
                    IconButton(onClick = { }) {
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

            // Interactive Stats Row as Tabs
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
                UserTab.PHOTOS -> renderPhotoItems(userPhotosState)
                UserTab.LIKES -> renderPhotoItems(userLikesState)
                UserTab.COLLECTIONS -> { // TODO: to be implemented
                }
            }
        }
    }
}

fun LazyStaggeredGridScope.renderPhotoItems(uiState: UiState<List<Photo>>) {
    when (uiState) {
        is UiState.Loading -> {
            // TODO: USe Blur hash
            items(6) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }

        is UiState.Error -> {
            item(span = StaggeredGridItemSpan.FullLine) {
                Text(
                    text = "Failed to load photos",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        is UiState.Content -> {
            val photos = uiState.data
            items(
                count = photos.size,
                key = { index -> photos[index].id }
            ) { index ->
                val photo = photos[index]
                AsyncImage(
                    model = photo.regularUrl,
                    contentDescription = photo.description ?: "User photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(
                            Random.nextDouble(0.8, 1.6).toFloat()
                        ) // An experimental feature
                        .clip(RoundedCornerShape(12.dp))
                )
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
        Text(count, style = MaterialTheme.typography.titleMedium)
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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