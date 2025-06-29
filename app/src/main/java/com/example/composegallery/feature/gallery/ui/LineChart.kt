package com.example.composegallery.feature.gallery.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composegallery.feature.gallery.domain.model.UserStatistics

@Composable
fun LineChart(
    values: List<Int>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Blue,
    pointColor: Color = Color.Red,
    strokeWidth: Dp = 2.dp
) {
    if (values.isEmpty()) {
        Box(modifier) {
            Text("No data", style = MaterialTheme.typography.bodySmall)
        }
        return
    }

    val maxValue = values.maxOrNull() ?: 0
    val minValue = values.minOrNull() ?: 0

    val paddingLeft = 40f
    val paddingBottom = 40f

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val chartWidth = width - paddingLeft
        val chartHeight = height - paddingBottom

        val stepX = chartWidth / (values.size - 1).coerceAtLeast(1)

        // Map values to points in chart area
        val points = values.mapIndexed { index, value ->
            val x = paddingLeft + index * stepX
            val y =
                chartHeight - ((value - minValue) / (maxValue - minValue).toFloat()) * chartHeight
            Offset(x, y)
        }

        val paintText = android.graphics.Paint().apply {
            textSize = 30f
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.RIGHT
            isAntiAlias = true
        }

        // Draw Y axis lines and labels (5 ticks)
        val tickCount = 5
        for (i in 0..tickCount) {
            val y = chartHeight - (chartHeight / tickCount) * i
            val valueAtTick = minValue + (maxValue - minValue) * i / tickCount
            drawLine(
                color = Color.LightGray,
                start = Offset(paddingLeft, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            // Draw Y axis label on left
            drawContext.canvas.nativeCanvas.drawText(
                valueAtTick.toString(),
                paddingLeft - 8,
                y + 10,
                paintText
            )
        }

        // Draw X axis line
        drawLine(
            color = Color.Black,
            start = Offset(paddingLeft, chartHeight),
            end = Offset(width, chartHeight),
            strokeWidth = 2f
        )

        val labelInterval = (values.size / 5).coerceAtLeast(1)
        for (i in values.indices step labelInterval) {
            val x = paddingLeft + i * stepX
            drawContext.canvas.nativeCanvas.drawText(
                i.toString(),
                x,
                chartHeight + 30,
                paintText
            )
        }

        // Draw lines between points
        for (i in 0 until points.size - 1) {
            drawLine(
                color = lineColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        }

        points.forEach { point ->
            drawCircle(
                color = pointColor,
                radius = 5f,
                center = point
            )
        }
    }
}

@Composable
fun UserStatsChart(
    stats: UserStatistics,
    modifier: Modifier = Modifier
) {
    Column(modifier.padding(16.dp)) {
        Text(
            "Downloads (Last ${stats.downloads.historical.quantity} days)",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        LineChart(
            values = stats.downloads.historical.values.map { it.value },
            lineColor = Color(0xFF4CAF50), // greenish
            pointColor = Color(0xFF388E3C),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Views (Last ${stats.views.historical.quantity} days)",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        LineChart(
            values = stats.views.historical.values.map { it.value },
            lineColor = Color(0xFF2196F3), // blue
            pointColor = Color(0xFF1976D2),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
    }
}

