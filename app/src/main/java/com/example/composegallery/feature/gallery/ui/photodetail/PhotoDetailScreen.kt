package com.example.composegallery.feature.gallery.ui.photodetail

import android.icu.util.TimeZone
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.ui.common.InfoMessageScreen
import com.example.composegallery.feature.gallery.ui.common.PhotoImage
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator
import com.example.composegallery.feature.gallery.ui.common.UserProfileImage
import com.example.composegallery.feature.gallery.ui.gallery.GalleryViewModel
import com.example.composegallery.feature.gallery.ui.util.UiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photoId: String,
    onBack: () -> Unit,
    onExpandClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val photoState by viewModel.uiState.collectAsState()
    val retryKey = remember(photoId) { mutableIntStateOf(0) }


    LaunchedEffect(photoId) {
        viewModel.loadPhoto(photoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.photo_details_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
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
                ProgressIndicator()
            }

            is UiState.Error -> {
                InfoMessageScreen(
                    imageRes = R.drawable.error_icon,
                    title = stringResource(R.string.error_failed_load_photo),
                    subtitle = stringResource(R.string.reason, state.message),
                    titleColor = MaterialTheme.colorScheme.error
                )
            }

            is UiState.Content -> {
                PhotoDetailContent(
                    photo = state.data,
                    retryKey = retryKey.intValue,
                    onRetry = { retryKey.intValue++ },
                    modifier = Modifier.padding(padding),
                    onExpandClick = onExpandClick,
                    onUserClick = onUserClick,
                )
            }
        }
    }
}

@Composable
private fun PhotoDetailContent(
    photo: Photo,
    retryKey: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onExpandClick: (String) -> Unit,
    onUserClick: (String) -> Unit
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
                imageUrl = "${photo.fullUrl}?retry=$retryKey",
                contentDescription = photo.authorName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(halfScreenHeightDp) // Force fixed height
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                blurHash = photo.blurHash,
                onRetry = onRetry
            )

            IconButton(
                onClick = { onExpandClick(photo.id) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = stringResource(R.string.view_full_screen),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(halfScreenHeightDp)
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                PhotoDetailInfo(
                    photo = photo,
                    onUserClick = onUserClick
                )
            }
        }
    }
}


@Composable
private fun PhotoDetailInfo(
    photo: Photo,
    onUserClick: (String) -> Unit
) {
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
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onUserClick(photo.username ?: photo.authorName) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserProfileImage(
                imageUrl = photo.authorProfileImageUrl,
                contentDescription = photo.authorName,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = photo.authorName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                photo.username?.let {
                    Text(
                        text = "@$it",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

        // Location
        photo.location?.let { location ->
            val locationText = listOfNotNull(location.city, location.country).joinToString(", ")
            if (locationText.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.photo_location_prefix) + " $locationText",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Photo Dimensions
        Text(
            text = stringResource(R.string.photo_size_label, photo.width, photo.height),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // Description
        photo.description?.takeIf { it.isNotBlank() }?.let { description ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.photo_description_label),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Created At
        formattedDate?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.photo_created_at_label),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = it,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // EXIF Info
        photo.exif?.let { exif ->
            val infoList = listOfNotNull(
                exif.make?.let { stringResource(R.string.camera_make) to it },
                exif.model?.let { stringResource(R.string.camera_model) to it },
                exif.aperture?.let { stringResource(R.string.camera_aperture) to it },
                exif.shutterSpeed?.let { stringResource(R.string.camera_shutter_speed) to it },
                exif.focalLength?.let { stringResource(R.string.camera_focal_length) to it },
                exif.iso?.let { stringResource(R.string.camera_iso) to it }
            )

            if (infoList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.camera_info_label),
                    style = MaterialTheme.typography.labelMedium
                )

                infoList.forEach { (label, value) ->
                    Text(
                        text = "$label: $value",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private fun String.formatToReadableDate(): String {
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
