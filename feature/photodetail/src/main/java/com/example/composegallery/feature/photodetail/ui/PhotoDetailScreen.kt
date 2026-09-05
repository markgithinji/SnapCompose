package com.example.composegallery.feature.photodetail.ui

import android.content.Intent
import android.icu.text.SimpleDateFormat
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composegallery.core.ui.R
import com.example.composegallery.core.model.DownloadStatus
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.ui.InfoMessageScreen
import com.example.composegallery.core.ui.PhotoImage
import com.example.composegallery.core.ui.ProgressIndicator
import com.example.composegallery.core.ui.SharedTransitionKeys
import com.example.composegallery.core.ui.UserProfileImage
import com.example.composegallery.core.util.UiState
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
    viewModel: PhotoDetailViewModel = hiltViewModel()
) {
    val photoState by viewModel.uiState.collectAsStateWithLifecycle()
    val isWallpaperLoading by viewModel.isWallpaperLoading.collectAsStateWithLifecycle()
    val downloadStatus by viewModel.downloadStatus.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val retryKey = remember(photoId) { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    var pendingAction by remember { mutableStateOf<PhotoDetailAction?>(null) }
    val context = LocalContext.current

    LaunchedEffect(photoId) {
        viewModel.loadPhoto(photoId)
    }

    LaunchedEffect(downloadStatus) {
        when (val status = downloadStatus) {
            is DownloadStatus.Success -> {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.download_success_path, status.path),
                    actionLabel = context.getString(R.string.dismiss),
                    duration = SnackbarDuration.Indefinite
                )
                viewModel.resetDownloadStatus()
            }
            is DownloadStatus.Error -> {
                snackbarHostState.showSnackbar(status.message)
                viewModel.resetDownloadStatus()
            }
            else -> {}
        }
    }

    if (pendingAction != null && photoState is UiState.Content) {
        val photo = (photoState as UiState.Content).data
        AlertDialog(
            onDismissRequest = { pendingAction = null },
            icon = {
                Icon(
                    imageVector = if (pendingAction == PhotoDetailAction.DOWNLOAD) Icons.Default.Download
                    else Icons.Default.Wallpaper,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(
                        if (pendingAction == PhotoDetailAction.DOWNLOAD) R.string.download_confirm_title
                        else R.string.wallpaper_confirm_title
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(
                        if (pendingAction == PhotoDetailAction.DOWNLOAD) R.string.download_confirm_msg
                        else R.string.wallpaper_confirm_msg
                    ),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = RoundedCornerShape(28.dp),
            confirmButton = {
                TextButton(
                    onClick = {
                        if (pendingAction == PhotoDetailAction.DOWNLOAD) {
                            viewModel.downloadPhoto(photo)
                        } else {
                            viewModel.setWallpaper(photo)
                        }
                        pendingAction = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.confirm),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
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
                val shareTitle = stringResource(R.string.share)
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
                    isWallpaperLoading = isWallpaperLoading,
                    downloadStatus = downloadStatus,
                    isFavorite = isFavorite,
                    onRetry = { retryKey.intValue++ },
                    modifier = Modifier.padding(padding),
                    onExpandClick = onExpandClick,
                    onUserClick = onUserClick,
                    onDownloadClick = { pendingAction = PhotoDetailAction.DOWNLOAD },
                    onWallpaperClick = { pendingAction = PhotoDetailAction.WALLPAPER },
                    onFavoriteClick = { viewModel.toggleFavorite(it) },
                    onShareClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                photo?.authorUnsplashUrl ?: photo?.regularUrl
                            )
                        }
                        context.startActivity(Intent.createChooser(intent, shareTitle))
                    },
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

private enum class PhotoDetailAction { DOWNLOAD, WALLPAPER }

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
    isWallpaperLoading: Boolean,
    downloadStatus: DownloadStatus,
    isFavorite: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onExpandClick: (String) -> Unit,
    onUserClick: (Photo) -> Unit,
    onDownloadClick: () -> Unit,
    onWallpaperClick: () -> Unit,
    onFavoriteClick: (Photo) -> Unit,
    onShareClick: () -> Unit,
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
            val baseImageUrl = photo?.regularUrl ?: initialThumbUrl ?: ""
            val imageUrl = if (photo != null && retryKey > 0) "$baseImageUrl?retry=$retryKey" else baseImageUrl
            
            PhotoImage(
                imageUrl = imageUrl,
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

            // Show the progress bar until the final high-quality image is fully loaded.
            // It stays visible if:
            // 1. Metadata is still loading (photo == null)
            // 2. The current image is still just the low-res thumbnail from the grid.
            // 3. Any network request is currently in progress.
            val isShowingThumbnail = initialThumbUrl != null && imageUrl == initialThumbUrl
            
            if (photo == null || isImageLoading || isShowingThumbnail) {
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
                        isWallpaperLoading = isWallpaperLoading,
                        downloadStatus = downloadStatus,
                        isFavorite = isFavorite,
                        onUserClick = onUserClick,
                        onDownloadClick = onDownloadClick,
                        onWallpaperClick = onWallpaperClick,
                        onFavoriteClick = { onFavoriteClick(photo) },
                        onShareClick = onShareClick
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
    isWallpaperLoading: Boolean,
    downloadStatus: DownloadStatus,
    isFavorite: Boolean,
    onUserClick: (Photo) -> Unit,
    onDownloadClick: () -> Unit,
    onWallpaperClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit
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
            UserProfileImage(
                imageUrl = photo.authorProfileImageHighResUrl,
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
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = if (isFavorite) MaterialTheme.colorScheme.primaryContainer 
                    else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (isFavorite) MaterialTheme.colorScheme.primary 
                    else MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null
                )
            }

            FilledTonalIconButton(
                onClick = onShareClick,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = stringResource(R.string.share))
            }

            val currentStatus = downloadStatus
            val isDownloading = currentStatus is DownloadStatus.Progress

            FilledTonalIconButton(
                onClick = onDownloadClick,
                modifier = Modifier.size(56.dp),
                enabled = !isDownloading,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isDownloading && currentStatus is DownloadStatus.Progress) {
                    val progress = currentStatus.percentage / 100f
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = LocalContentColor.current
                        )
                        Text(
                            text = "${currentStatus.percentage}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = LocalContentColor.current
                        )
                    }
                } else {
                    Icon(Icons.Default.Download, contentDescription = stringResource(R.string.download))
                }
            }

            FilledIconButton(
                onClick = onWallpaperClick,
                modifier = Modifier.size(56.dp),
                enabled = !isWallpaperLoading,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isWallpaperLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = LocalContentColor.current
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Wallpaper,
                        contentDescription = stringResource(R.string.set_as_wallpaper)
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
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = isoFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this // fallback to original if parsing fails
    }
}
