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
import com.example.composegallery.feature.gallery.domain.model.UserStatistics
import com.example.composegallery.feature.gallery.ui.common.InfoMessageScreen
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator
import com.example.composegallery.feature.gallery.ui.util.UiState


@Composable
fun UserProfileScreen(
    username: String,
    onBack: () -> Unit,
    onPhotoClick: (String) -> Unit,
    onCollectionClick: (String, String) -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val userProfileState = viewModel.userProfileState.collectAsStateWithLifecycle().value
    val userStatsState = viewModel.userStatisticsState.collectAsStateWithLifecycle().value
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
            InfoMessageScreen(
                imageRes = R.drawable.error_icon,
                title = "Failed to load user profile",
                subtitle = "Reason: ${userProfileState.message}",
                titleColor = MaterialTheme.colorScheme.error
            )
        }

        is UiState.Content -> {
            val user = userProfileState.data
            UserProfileContent(
                name = user.name,
                bio = user.bio,
                location = user.location,
                portfolioUrl = user.portfolioUrl,
                instagramUsername = user.instagramUsername,
                totalPhotos = user.totalPhotos,
                totalLikes = user.totalLikes,
                totalCollections = user.totalCollections,
                profileImageUrl = user.profileImageLarge,
                userPhotos = photos,
                userLikes = userLikes,
                userCollections = collections,
                onPhotoClick = onPhotoClick,
                onCollectionClick = onCollectionClick,
                onStatsClick = { showStatsDialog = true },
                onBack = onBack,
            )
        }
    }

    // Show the stats dialog if requested
    if (showStatsDialog) {
        UserStatsDialog(
            state = userStatsState,
            onDismiss = { showStatsDialog = false }
        )
    }
}

@Composable
private fun UserStatsDialog(
    state: UiState<UserStatistics>,
    onDismiss: () -> Unit
) {
    when (state) {
        is UiState.Content -> {
            val stats = state.data
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("User Statistics", style = MaterialTheme.typography.headlineSmall) },
                text = {
                    UserStatsChart(
                        downloadsValues = stats.downloads.historical.values.map { it.value.toFloat() },
                        downloadsDays = stats.downloads.historical.quantity,
                        viewsValues = stats.views.historical.values.map { it.value.toFloat() },
                        viewsDays = stats.views.historical.quantity,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                },
                confirmButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            )
        }

        is UiState.Loading -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Text(
                        "Loading Stats...",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    ProgressIndicator()
                },
                confirmButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            )
        }

        is UiState.Error -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Text(
                        text = "Error Loading Stats",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error icon",
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                confirmButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

