package com.example.composegallery.feature.gallery.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.UserStatistics
import com.example.composegallery.feature.gallery.ui.common.InfoMessageScreen
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator
import com.example.composegallery.feature.gallery.ui.util.UiState


@Composable
fun UserProfileScreen(
    username: String,
    onBack: () -> Unit,
    onPhotoClick: (Photo) -> Unit,
    onCollectionClick: (String, String, Int) -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val userProfileState = viewModel.userProfileState.collectAsStateWithLifecycle().value
    val userStatsState = viewModel.userStatisticsState.collectAsStateWithLifecycle().value
    val photos = viewModel.userPhotos.collectAsLazyPagingItems()
    val userLikes = viewModel.userLikedPhotos.collectAsLazyPagingItems()
    val collections = viewModel.userCollectionsState.collectAsLazyPagingItems()

    var showStatsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(username) {
        viewModel.setUsername(username)
    }

    when (userProfileState) {
        is UiState.Loading -> {
            ProgressIndicator(modifier = Modifier.fillMaxSize())
        }

        is UiState.Error -> {
            InfoMessageScreen(
                imageRes = R.drawable.error_icon,
                title = stringResource(R.string.failed_to_load_user_profile),
                subtitle = stringResource(R.string.reason, userProfileState.message),
                titleColor = MaterialTheme.colorScheme.error
            )
        }

        is UiState.Content -> {
            val user = userProfileState.data
            UserProfileContent(
                username = username,
                name = user.name,
                bio = user.bio,
                location = user.location,
                portfolioUrl = user.portfolioUrl,
                instagramUsername = user.instagramUsername,
                totalPhotos = user.totalPhotos,
                totalLikes = user.totalLikes,
                totalCollections = user.totalCollections,
                profileImageUrl = user.profileImageLarge,
                unsplashProfileUrl = user.unsplashProfileUrl,
                userPhotos = photos,
                userLikes = userLikes,
                userCollections = collections,
                onPhotoClick = onPhotoClick,
                onCollectionClick = onCollectionClick,
                onStatsClick = { showStatsDialog = true },
                onBack = onBack,
                onTabSelected = { tab ->
                    // Navigation logic handled reactively in ViewModel
                }
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
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.user_statistics),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                when (state) {
                    is UiState.Content -> {
                        UserStatsChart(
                            statistics = state.data,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ProgressIndicator()
                        }
                    }
                    is UiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
