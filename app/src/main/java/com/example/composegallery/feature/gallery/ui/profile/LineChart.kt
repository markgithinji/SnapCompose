package com.example.composegallery.feature.gallery.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composegallery.feature.gallery.domain.model.HistoricalData
import com.example.composegallery.feature.gallery.domain.model.StatData
import com.example.composegallery.feature.gallery.domain.model.StatValue
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
            drawContext.canvas.nativeCanvas.drawText(
                formatNumber(valueAtTick),
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

        // Draw X axis labels (5 segments)
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

        // Draw points
        points.forEach { point ->
            drawCircle(
                color = pointColor,
                radius = 5f,
                center = point
            )
        }
    }
}


private fun formatNumber(value: Int): String {
    val absValue = kotlin.math.abs(value)
    return when {
        absValue >= 1_000_000 -> String.format("%.1fM", value / 1_000_000f)
        absValue >= 1_000 -> String.format("%.1fk", value / 1_000f)
        else -> value.toString()
    }
}

@Composable
fun UserStatsChart(
    stats: UserStatistics,
    modifier: Modifier = Modifier
) {
    Column(modifier.padding(16.dp)) {
        Text(
            text = "Downloads (Last ${stats.downloads.historical.quantity} days)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(Modifier.height(8.dp))
        LineChart(
            values = stats.downloads.historical.values.map { it.value },
            lineColor = MaterialTheme.colorScheme.primaryContainer,
            pointColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Views (Last ${stats.views.historical.quantity} days)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(Modifier.height(8.dp))
        LineChart(
            values = stats.views.historical.values.map { it.value },
            lineColor = MaterialTheme.colorScheme.secondaryContainer,
            pointColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserStatsChart() {
    val mockStats = UserStatistics(
        username = "john_doe",
        downloads = StatData(
            total = 150_000,
            historical = HistoricalData(
                change = 5,
                average = 5000,
                resolution = "days",
                quantity = 30,
                values = List(30) { index ->
                    StatValue(
                        date = "2024-06-${(index + 1).coerceAtMost(30)}",
                        value = (4000..6000).random()
                    )
                }
            )
        ),
        views = StatData(
            total = 1_200_000,
            historical = HistoricalData(
                change = 12,
                average = 40000,
                resolution = "days",
                quantity = 30,
                values = List(30) { index ->
                    StatValue(
                        date = "2024-06-${(index + 1).coerceAtMost(30)}",
                        value = (30000..50000).random()
                    )
                }
            )
        )
    )

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            UserStatsChart(stats = mockStats)
        }
    }
}
