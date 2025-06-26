package com.example.composegallery.feature.gallery.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun GalleryScreen(viewModel: GalleryViewModel = hiltViewModel()) {
    val photos = viewModel.pagedPhotos.collectAsLazyPagingItems()
    val isRefreshing = photos.loadState.refresh is LoadState.Loading
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    var query by rememberSaveable { mutableStateOf("") }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { photos.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            AnimatedContent(
                targetState = photos.loadState.refresh,
                transitionSpec = {
                    fadeIn(tween(500)) togetherWith fadeOut(tween(300))
                },
                label = "PhotoGridTransition"
            ) { state ->
                when (state) {
                    is LoadState.Loading -> {
                        ProgressIndicator()
                    }

                    is LoadState.Error -> {
                        val error = state.error.localizedMessage
                        DataNotFoundAnim(
                            message = error ?: "Unknown error",
                            onRetry = { photos.retry() }
                        )
                    }

                    else -> {
                        PhotoGrid(
                            photos = photos,
                            query = query,
                            onQueryChange = { query = it },
                            onSearch = { photos.refresh() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator()
    }
}

@Composable
fun DataNotFoundAnim(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Failed to load images:\n$message",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}