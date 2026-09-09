package com.example.composegallery.feature.photodetail.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.composegallery.core.ui.R
import com.example.composegallery.core.ui.InfoMessageScreen
import com.example.composegallery.core.ui.ProgressIndicator
import com.example.composegallery.core.common.UiState

@Composable
fun PhotoViewerScreen(
    photoId: String,
    viewModel: PhotoDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val view = LocalView.current
    val context = LocalContext.current

    SideEffect {
        view.isHapticFeedbackEnabled = true
    }

    LaunchedEffect(photoId) {
        viewModel.loadPhoto(photoId)
    }

    when (val uiState = state) {
        is UiState.Content -> {
            val photo = uiState.data

            val containerSize = LocalWindowInfo.current.containerSize
            var scale by rememberSaveable { mutableFloatStateOf(1f) }
            var rotation by rememberSaveable { mutableFloatStateOf(0f) }
            var offset by rememberSaveable(
                stateSaver = listSaver(
                    save = { listOf(it.x, it.y) },
                    restore = { Offset(it[0], it[1]) }
                )
            ) { mutableStateOf(Offset.Zero) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                val containerWidth = containerSize.width.toFloat()
                val containerHeight = containerSize.height.toFloat()

                val maxX = ((scale - 1f) * containerWidth) / 2f
                val maxY = ((scale - 1f) * containerHeight) / 2f
                val minZoom = 1f
                val maxZoom = 5f

                val transformableState =
                    rememberTransformableState { _, zoomChange, offsetChange, rotationChange ->
                        scale = (scale * zoomChange).coerceIn(
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
                    onSuccess = {
                        photo.downloadLocationUrl?.let { url ->
                            viewModel.reportDownload(url)
                        }
                    },
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ProgressIndicator(color = Color.White)
                        }
                    },
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
                                performHapticFeedback(view, context, HapticFeedbackConstants.VIRTUAL_KEY)
                                scale = 1f
                                rotation = 0f
                                offset = Offset.Zero
                            })
                        }
                )
            }
        }

        is UiState.Loading ->
            ProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                color = Color.White
            )

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

@SuppressLint("MissingPermission")
private fun performHapticFeedback(view: View, context: Context, constant: Int) {
    val success = view.performHapticFeedback(
        constant,
        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
    )
    if (!success) {
        val vibrator = context.getSystemService(Vibrator::class.java)
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(10)
            }
        }
    }
}