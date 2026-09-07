package com.example.composegallery.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
        modifier = modifier.size(indicatorSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasSize = size.minDimension
            val canvasCenter = Offset(size.width / 2, size.height / 2)
            
            // Reduced base sizes to ensure no clipping during 45-degree rotation + 1.2x scale
            val squareSizeBase = canvasSize / 5f 
            val currentSquareSize = squareSizeBase * scale
            
            // Distance from canvas center to square center
            val distanceFromCenter = canvasSize / 5.5f

            // CRITICAL: Explicitly set the pivot to the canvas center to avoid "oval" paths
            rotate(degrees = rotation, pivot = canvasCenter) {
                // Top Left
                drawRoundRect(
                    color = color.copy(alpha = 0.9f),
                    topLeft = Offset(
                        canvasCenter.x - distanceFromCenter - currentSquareSize / 2,
                        canvasCenter.y - distanceFromCenter - currentSquareSize / 2
                    ),
                    size = Size(currentSquareSize, currentSquareSize),
                    cornerRadius = CornerRadius(currentSquareSize * 0.25f)
                )
                
                // Top Right
                drawRoundRect(
                    color = color.copy(alpha = 0.6f),
                    topLeft = Offset(
                        canvasCenter.x + distanceFromCenter - currentSquareSize / 2,
                        canvasCenter.y - distanceFromCenter - currentSquareSize / 2
                    ),
                    size = Size(currentSquareSize, currentSquareSize),
                    cornerRadius = CornerRadius(currentSquareSize * 0.25f)
                )
                
                // Bottom Left
                drawRoundRect(
                    color = color.copy(alpha = 0.4f),
                    topLeft = Offset(
                        canvasCenter.x - distanceFromCenter - currentSquareSize / 2,
                        canvasCenter.y + distanceFromCenter - currentSquareSize / 2
                    ),
                    size = Size(currentSquareSize, currentSquareSize),
                    cornerRadius = CornerRadius(currentSquareSize * 0.25f)
                )
                
                // Bottom Right
                drawRoundRect(
                    color = color.copy(alpha = 0.2f),
                    topLeft = Offset(
                        canvasCenter.x + distanceFromCenter - currentSquareSize / 2,
                        canvasCenter.y + distanceFromCenter - currentSquareSize / 2
                    ),
                    size = Size(currentSquareSize, currentSquareSize),
                    cornerRadius = CornerRadius(currentSquareSize * 0.25f)
                )
            }
        }
    }
}
