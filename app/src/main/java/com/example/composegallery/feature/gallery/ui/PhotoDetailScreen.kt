package com.example.composegallery.feature.gallery.ui

import android.icu.util.TimeZone
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.composegallery.feature.gallery.domain.model.Photo
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photoId: String,
    onBack: () -> Unit,
    viewModel: PhotoDetailViewModel = hiltViewModel()
) {
    val photoState by viewModel.uiState.collectAsState()

    LaunchedEffect(photoId) {
        viewModel.loadPhoto(photoId)
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
        when (val state = photoState) {
            UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    ProgressIndicator()
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Failed to load photo: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is UiState.Content -> {
                PhotoDetailContent(
                    photo = state.data,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun PhotoDetailContent(photo: Photo, modifier: Modifier = Modifier) {
    val containerSize = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current
    val halfScreenHeightDp = with(density) { (containerSize.height * 0.5f).toDp() }

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(halfScreenHeightDp)
        ) {
            PhotoImage(
                imageUrl = photo.fullUrl,
                aspectRatio = 1f, // will be overridden by parent height
                contentDescription = photo.authorName,
                blurHash = photo.blurHash,
                onRetry = {}
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(halfScreenHeightDp)
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                PhotoDetailInfo(photo = photo)
            }
        }
    }
}


@Composable
fun PhotoDetailInfo(photo: Photo) {
    val formattedDate by remember(photo.createdAt) {
        derivedStateOf {
            photo.createdAt?.formatToReadableDate()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Author
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = photo.authorProfileImageUrl,
                contentDescription = "Author Image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = photo.authorName,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Photo Info
        Text("Photo ID: ${photo.id}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Size: ${photo.width} x ${photo.height}", style = MaterialTheme.typography.bodySmall)

        // Description
        photo.description?.takeIf { it.isNotBlank() }?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Description", style = MaterialTheme.typography.titleSmall)
            Text(it, style = MaterialTheme.typography.bodySmall)
        }

        // Created At
        formattedDate?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Created at", style = MaterialTheme.typography.titleSmall)
            Text(it, style = MaterialTheme.typography.bodySmall)
        }

        // EXIF
        photo.exif?.let { exif ->
            Spacer(modifier = Modifier.height(12.dp))
            Text("Camera Info", style = MaterialTheme.typography.titleSmall)

            exif.make?.let { Text("Make: $it", style = MaterialTheme.typography.bodySmall) }
            exif.model?.let { Text("Model: $it", style = MaterialTheme.typography.bodySmall) }
            exif.aperture?.let { Text("Aperture: $it", style = MaterialTheme.typography.bodySmall) }
            exif.shutterSpeed?.let {
                Text(
                    "Shutter Speed: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            exif.focalLength?.let {
                Text(
                    "Focal Length: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            exif.iso?.let { Text("ISO: $it", style = MaterialTheme.typography.bodySmall) }
        }
    }
}

fun String.formatToReadableDate(): String {
    return try {
        val isoFormat =
            android.icu.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        val outputFormat = android.icu.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = isoFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this // fallback to original if parsing fails
    }
}
