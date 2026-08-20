package com.example.composegallery.feature.gallery.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    indicatorSize: Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(indicatorSize)
        ) {
            val canvasSize = size.width
            val squareSize = canvasSize / 3
            
            rotate(rotation) {
                // Draw 4 "pixels" in a square formation that pulse
                val padding = (canvasSize * 0.08f)
                
                // Top Left
                drawRoundRect(
                    color = color.copy(alpha = 0.9f),
                    topLeft = Offset(padding, padding),
                    size = Size(squareSize * scale, squareSize * scale),
                    cornerRadius = CornerRadius(squareSize * 0.2f)
                )
                
                // Top Right
                drawRoundRect(
                    color = color.copy(alpha = 0.6f),
                    topLeft = Offset(canvasSize - squareSize * scale - padding, padding),
                    size = Size(squareSize * scale, squareSize * scale),
                    cornerRadius = CornerRadius(squareSize * 0.2f)
                )
                
                // Bottom Left
                drawRoundRect(
                    color = color.copy(alpha = 0.4f),
                    topLeft = Offset(padding, canvasSize - squareSize * scale - padding),
                    size = Size(squareSize * scale, squareSize * scale),
                    cornerRadius = CornerRadius(squareSize * 0.2f)
                )
                
                // Bottom Right
                drawRoundRect(
                    color = color.copy(alpha = 0.2f),
                    topLeft = Offset(canvasSize - squareSize * scale - padding, canvasSize - squareSize * scale - padding),
                    size = Size(squareSize * scale, squareSize * scale),
                    cornerRadius = CornerRadius(squareSize * 0.2f)
                )
            }
        }
    }
}
