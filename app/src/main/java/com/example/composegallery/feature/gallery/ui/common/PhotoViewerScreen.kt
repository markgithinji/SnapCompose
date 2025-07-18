package com.example.composegallery.feature.gallery.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.ui.gallery.GalleryViewModel
import com.example.composegallery.feature.gallery.ui.util.UiState

@Composable
fun PhotoViewerScreen(
    photoId: String,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(photoId) {
        viewModel.loadPhoto(photoId)
    }

    when (val uiState = state) {
        is UiState.Content -> {
            val photo = uiState.data

            var scale by remember { mutableFloatStateOf(1f) }
            var rotation by remember { mutableFloatStateOf(0f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                val containerWidth = constraints.maxWidth.toFloat()
                val containerHeight = constraints.maxHeight.toFloat()

                val maxX = ((scale - 1f) * containerWidth) / 2f
                val maxY = ((scale - 1f) * containerHeight) / 2f
                val minZoom = 1f
                val maxZoom = 5f

                val transformableState =
                    rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                        scale = (scale * zoomChange).coerceIn( // Limit zoom to bounds of the image
                            minZoom,
                            maxZoom
                        )
                        rotation += rotationChange

                        val newOffset = offset + offsetChange
                        offset = Offset(
                            x = newOffset.x.coerceIn(-maxX, maxX),
                            y = newOffset.y.coerceIn(-maxY, maxY)
                        )
                    }

                SubcomposeAsyncImage(
                    model = photo.fullUrl,
                    contentDescription = photo.description
                        ?: stringResource(R.string.photo_zoom_description),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .transformable(transformableState)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                            rotationZ = rotation
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = {
                                scale = 1f
                                rotation = 0f
                                offset = Offset.Zero
                            })
                        }
                )
            }
        }

        is UiState.Loading ->
            ProgressIndicator()

        is UiState.Error -> {
            InfoMessageScreen(
                title = stringResource(R.string.error_photo_load_title),
                subtitle = "Reason: ${uiState.message}",
                imageRes = R.drawable.error_icon,
                titleColor = MaterialTheme.colorScheme.error
            )
        }
    }
}
