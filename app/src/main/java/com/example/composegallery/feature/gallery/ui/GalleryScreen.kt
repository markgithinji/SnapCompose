package com.example.composegallery.feature.gallery.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage


@Composable
fun GalleryScreen(viewModel: GalleryViewModel = hiltViewModel()) {
    val photos by viewModel.photos.collectAsStateWithLifecycle()

    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(photos) { photo ->
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

@Preview(showBackground = true)
@Composable
fun GalleryScreenPreview() {
//    GalleryScreen(images = mockImages)
}
