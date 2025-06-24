package com.example.composegallery.feature.gallery.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun GalleryScreen(viewModel: GalleryViewModel = hiltViewModel()) {
    val photos = viewModel.pagedPhotos.collectAsLazyPagingItems()

    when (photos.loadState.refresh) {
        is LoadState.Loading -> {
            ProgressIndicator()
        }

        is LoadState.Error -> {
            val error = (photos.loadState.refresh as LoadState.Error).error.localizedMessage
            DataNotFoundAnim(message = error ?: "Unknown error")
        }

        else -> {
            PhotoGrid(photos)
        }
    }
}

@Composable
fun ProgressIndicator() {
    // TODO: Improve progress indicator
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
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