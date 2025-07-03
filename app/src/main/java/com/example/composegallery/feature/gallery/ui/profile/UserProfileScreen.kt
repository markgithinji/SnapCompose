package com.example.composegallery.feature.gallery.ui.profile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import com.example.composegallery.feature.gallery.domain.model.UserStatistics
import com.example.composegallery.feature.gallery.ui.common.MessageScreen
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator
import com.example.composegallery.feature.gallery.ui.util.UiState


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
    val photos = viewModel.userPhotos.collectAsLazyPagingItems()
    val userLikes = viewModel.userLikedPhotos.collectAsLazyPagingItems()
    val collections = viewModel.userCollectionsState.collectAsLazyPagingItems()

    var showStatsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(username) { // TODO: Load each item on request
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
            MessageScreen(
                imageRes = R.drawable.error_icon,
                title = "Failed to load user profile",
                subtitle = (userProfileState as UiState.Error).message,
                titleColor = MaterialTheme.colorScheme.error
            )
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
        when (userStatsState) {
            is UiState.Content -> {
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
            }

            is UiState.Loading -> {
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
            }

            is UiState.Error -> {
                AlertDialog(
                    onDismissRequest = { showStatsDialog = false },
                    title = {
                        Text(
                            text = "Error Loading Stats",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { showStatsDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}