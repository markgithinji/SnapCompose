package com.example.composegallery.feature.gallery.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.composegallery.feature.gallery.domain.model.Photo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photoId: String,
    onBack: () -> Unit
) {
    // Replace this
    val dummyPhoto = remember(photoId) {
        Photo(
            id = photoId,
            width = 1080,
            height = 720,
            thumbUrl = "",
            fullUrl = "https://source.unsplash.com/random/800x600?sig=$photoId",
            authorName = "Jane Doe",
            authorProfileImageUrl = "https://randomuser.me/api/portraits/women/1.jpg",
            blurHash = null
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photo Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Photo: take up 3/4 of the screen height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
            ) {
                PhotoImage(
                    imageUrl = dummyPhoto.fullUrl,
                    aspectRatio = dummyPhoto.width.toFloat() / dummyPhoto.height,
                    contentDescription = dummyPhoto.authorName,
                    blurHash = dummyPhoto.blurHash,
                    onRetry = {}
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = dummyPhoto.authorProfileImageUrl,
                        contentDescription = "Author Image",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dummyPhoto.authorName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Photo ID: ${dummyPhoto.id}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Size: ${dummyPhoto.width} x ${dummyPhoto.height}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}