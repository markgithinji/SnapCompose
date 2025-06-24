package com.example.composegallery.feature.gallery.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.composegallery.feature.gallery.domain.model.Photo

@Composable
fun GalleryScreen(viewModel: GalleryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState.let {
        when (it) {
            UiState.Loading -> ProgressIndicator()
            is UiState.Error -> DataNotFoundAnim(it.message)
            is UiState.Content -> PhotoGrid(it.data)
        }
    }
}

@Composable
fun PhotoGrid(photos: List<Photo>) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(photos, key = { it.id }) { photo ->
            Column(modifier = Modifier.padding(8.dp)) {
                AsyncImage(
                    model = photo.imageUrl,
                    contentDescription = photo.authorName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
                Text(photo.authorName, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


@Composable
fun ProgressIndicator() {
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


@Preview(showBackground = true)
@Composable
fun GalleryScreenPreview() {
//    GalleryScreen(images = mockImages)
}
