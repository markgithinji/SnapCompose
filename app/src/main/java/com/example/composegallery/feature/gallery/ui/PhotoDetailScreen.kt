package com.example.composegallery.feature.gallery.ui

import android.icu.util.TimeZone
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
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
    onExpandClick: (String) -> Unit,
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
                    modifier = Modifier.padding(padding),
                    onExpandClick = onExpandClick
                )
            }
        }
    }
}

@Composable
fun PhotoDetailContent(
    photo: Photo,
    modifier: Modifier = Modifier,
    onExpandClick: (String) -> Unit
) {
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
                contentDescription = photo.authorName,
                blurHash = photo.blurHash,
                onRetry = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(halfScreenHeightDp) // Force fixed height
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            )
            IconButton(
                onClick = { onExpandClick(photo.id) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "View Full Screen"
                )
            }
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
        derivedStateOf { photo.createdAt?.formatToReadableDate() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Author Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = photo.authorProfileImageHighResUrl,
                contentDescription = "${photo.authorName}'s profile picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = photo.authorName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                photo.authorInstagramUsername?.let {
                    Text(
                        text = "@$it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Photo Dimensions
        Text(
            text = "Size: ${photo.width} × ${photo.height}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Location
        photo.location?.let { location ->
            val locationText = listOfNotNull(location.city, location.country).joinToString(", ")
            if (locationText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📍 $locationText",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Description
        photo.description?.takeIf { it.isNotBlank() }?.let { description ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Created At
        formattedDate?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Created At",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // EXIF Info
        photo.exif?.let { exif ->
            val infoList = listOfNotNull(
                exif.make?.let { "Make" to it },
                exif.model?.let { "Model" to it },
                exif.aperture?.let { "Aperture" to it },
                exif.shutterSpeed?.let { "Shutter Speed" to it },
                exif.focalLength?.let { "Focal Length" to it },
                exif.iso?.let { "ISO" to it }
            )

            if (infoList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Camera Info",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium)
                )

                infoList.forEach { (label, value) ->
                    Text(
                        text = "$label: $value",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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
