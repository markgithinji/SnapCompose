package com.example.composegallery.feature.gallery.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.composegallery.feature.gallery.domain.model.Photo

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
fun PhotoGrid(photos: LazyPagingItems<Photo>) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(photos.itemCount) { index ->
            val photo = photos[index]
            if (photo != null) {
                Column(modifier = Modifier.padding(8.dp)) {
                    AsyncImage(
                        model = photo.thumbUrl,
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

        when (photos.loadState.append) {
            is LoadState.Loading -> item(span = { GridItemSpan(2) }) {
                ProgressIndicator()
            }

            is LoadState.Error -> item(span = { GridItemSpan(2) }) {
                val error = (photos.loadState.append as LoadState.Error).error.localizedMessage
                DataNotFoundAnim(message = error ?: "Error loading more")
            }

            else -> Unit
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
