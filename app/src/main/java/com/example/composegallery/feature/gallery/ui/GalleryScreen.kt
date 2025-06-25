package com.example.composegallery.feature.gallery.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
            when (photos.loadState.refresh) {
                is LoadState.Loading -> {
                    ProgressIndicator()
                }

                is LoadState.Error -> {
                    val error = (photos.loadState.refresh as LoadState.Error).error.localizedMessage
                    DataNotFoundAnim(message = error ?: "Unknown error")
                }

                else -> {
                    PhotoGrid(
                        photos = photos,
                        query = "",
                        onQueryChange = {},
                        onSearch = {}
                    )
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
    // TODO: Improve progress indicator
//    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        CircularProgressIndicator()
//    }
}

@Composable
fun DataNotFoundAnim(message: String) {
    // TODO: add not-found lottie animation
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Failed to load images: $message",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}