package com.example.composegallery.feature.gallery.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.composegallery.feature.gallery.ui.gallery.GalleryViewModel
import com.example.composegallery.feature.gallery.ui.util.UiState
import com.example.composegallery.feature.gallery.ui.gallery.ProgressIndicator

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

                val transformableState =
                    rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                        scale = (scale * zoomChange).coerceIn(1f, 5f) // Limit zoom to bounds of the image
                        rotation += rotationChange

                        val newOffset = offset + offsetChange
                        offset = Offset(
                            x = newOffset.x.coerceIn(-maxX, maxX),
                            y = newOffset.y.coerceIn(-maxY, maxY)
                        )
                    }

                SubcomposeAsyncImage(
                    model = photo.fullUrl,
                    contentDescription = "Full screen photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                            rotationZ = rotation
                        }
                        .transformable(transformableState)
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

        is UiState.Loading -> ProgressIndicator()
        is UiState.Error -> Text("Error loading photo", color = Color.Red)
    }
}
