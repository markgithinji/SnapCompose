package com.example.composegallery.feature.gallery.ui.photodetail

import android.icu.util.TimeZone
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.ui.common.InfoMessageScreen
import com.example.composegallery.feature.gallery.ui.common.PhotoImage
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator
import com.example.composegallery.feature.gallery.ui.common.SharedTransitionKeys
import com.example.composegallery.feature.gallery.ui.common.UserProfileImage
import com.example.composegallery.feature.gallery.ui.gallery.GalleryViewModel
import com.example.composegallery.feature.gallery.ui.util.UiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoDetailScreen(
    photoId: String,
    initialWidth: Int,
    initialHeight: Int,
    initialThumbUrl: String? = null,
    initialBlurHash: String? = null,
    origin: String = "gallery",
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    onBack: () -> Unit,
    onExpandClick: (String) -> Unit,
    onUserClick: (Photo) -> Unit,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val photoState by viewModel.uiState.collectAsStateWithLifecycle()
    val isActionLoading by viewModel.isActionLoading.collectAsStateWithLifecycle()
    val retryKey = remember(photoId) { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(photoId) {
        viewModel.loadPhoto(photoId)
    }

    LaunchedEffect(Unit) {
        viewModel.actionEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.testTag("PhotoDetailScreen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
        val detailShape = RoundedCornerShape(24.dp)

        when (val state = photoState) {
            is UiState.Error -> {
                InfoMessageScreen(
                    imageRes = R.drawable.error_icon,
                    title = stringResource(R.string.error_failed_load_photo),
                    subtitle = stringResource(R.string.reason, state.message),
                    titleColor = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                val photo = (state as? UiState.Content)?.data
                PhotoDetailContent(
                    photo = photo,
                    photoId = photoId,
                    initialWidth = initialWidth,
                    initialHeight = initialHeight,
                    initialThumbUrl = initialThumbUrl,
                    initialBlurHash = initialBlurHash,
                    origin = origin,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    retryKey = retryKey.intValue,
                    isActionLoading = isActionLoading,
                    onRetry = { retryKey.intValue++ },
                    modifier = Modifier.padding(padding),
                    onExpandClick = onExpandClick,
                    onUserClick = onUserClick,
                    onDownloadClick = { viewModel.downloadPhoto(it) },
                    onWallpaperClick = { viewModel.setWallpaper(it) },
                    shape = detailShape,
                    onImageLoad = {
                        photo?.downloadLocationUrl?.let { url ->
                            viewModel.reportDownload(url)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PhotoDetailContent(
    photo: Photo?,
    photoId: String,
    initialWidth: Int,
    initialHeight: Int,
    initialThumbUrl: String?,
    initialBlurHash: String?,
    origin: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    retryKey: Int,
    isActionLoading: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onExpandClick: (String) -> Unit,
    onUserClick: (Photo) -> Unit,
    onDownloadClick: (Photo) -> Unit,
    onWallpaperClick: (Photo) -> Unit,
    shape: RoundedCornerShape,
    onImageLoad: () -> Unit
) {
    val containerSize = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current
    val halfScreenHeightDp = with(density) { (containerSize.height * 0.5f).toDp() }
    var isImageLoading by remember { mutableStateOf(true) }

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(halfScreenHeightDp)
        ) {
            val imageUrl = photo?.fullUrl ?: initialThumbUrl ?: ""
            PhotoImage(
                imageUrl = if (photo != null) "$imageUrl?retry=$retryKey" else imageUrl,
                contentDescription = photo?.authorName ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(halfScreenHeightDp),
                blurHash = photo?.blurHash ?: initialBlurHash,
                shape = shape,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedKey = SharedTransitionKeys.photoImage(photoId, origin),
                placeholderUrl = initialThumbUrl,
                onLoading = { isImageLoading = it },
                onSuccess = onImageLoad,
                onRetry = onRetry
            )

            if (isImageLoading || photo == null) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            }

            if (photo != null) {
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
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(halfScreenHeightDp)
        ) {
            if (photo != null) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    PhotoDetailInfo(
                        photo = photo,
                        origin = origin,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        isActionLoading = isActionLoading,
                        onUserClick = onUserClick,
                        onDownloadClick = onDownloadClick,
                        onWallpaperClick = onWallpaperClick
                    )
                }
            } else {
                ProgressIndicator(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PhotoDetailInfo(
    photo: Photo,
    origin: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    isActionLoading: Boolean,
    onUserClick: (Photo) -> Unit,
    onDownloadClick: (Photo) -> Unit,
    onWallpaperClick: (Photo) -> Unit
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
                .clickable { onUserClick(photo) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            val userSharedKey = if (!origin.startsWith("profile")) {
                SharedTransitionKeys.userProfileImage(photo.username ?: photo.authorName)
            } else {
                null
            }

            UserProfileImage(
                imageUrl = photo.authorProfileImageHighResUrl,
                contentDescription = photo.authorName,
                modifier = Modifier.size(48.dp),
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedKey = userSharedKey
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = photo.authorName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    photo.username?.let {
                        Text(
                            text = "@$it",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    photo.authorUnsplashUrl?.let { url ->
                        val uriHandler = LocalUriHandler.current
                        val appName = stringResource(R.string.app_name)
                        if (photo.username != null) {
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Unsplash",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                val utmUrl = if (url.contains("?")) {
                                    "$url&utm_source=$appName&utm_medium=referral"
                                } else {
                                    "$url?utm_source=$appName&utm_medium=referral"
                                }
                                uriHandler.openUri(utmUrl)
                            }
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onDownloadClick(photo) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.download))
            }

            Button(
                onClick = { onWallpaperClick(photo) },
                modifier = Modifier.weight(1f),
                enabled = !isActionLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isActionLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Wallpaper, contentDescription = null)
                }
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.set_as_wallpaper))
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
