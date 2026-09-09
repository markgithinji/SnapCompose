package com.example.composegallery.feature.profile.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composegallery.core.ui.R
import com.example.composegallery.core.domain.model.HistoricalData
import com.example.composegallery.core.domain.model.StatData
import com.example.composegallery.core.domain.model.StatValue
import com.example.composegallery.core.domain.model.UserStatistics
import java.util.Locale
import kotlin.math.abs

@Composable
fun UserStatsChart(
    statistics: UserStatistics,
    modifier: Modifier = Modifier
) {
    val noDataText = stringResource(R.string.no_data)

    Column(modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
        StatHeader(
            title = stringResource(R.string.downloads_last_days, statistics.downloads.historical.quantity),
            total = statistics.downloads.total,
            icon = Icons.Default.FileDownload,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        ModernLineChart(
            values = statistics.downloads.historical.values.map { it.value.toFloat() },
            labels = statistics.downloads.historical.values.map { it.date },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            primaryColor = MaterialTheme.colorScheme.primary,
            noDataText = noDataText
        )

        Spacer(Modifier.height(24.dp))

        StatHeader(
            title = stringResource(R.string.views_last_days, statistics.views.historical.quantity),
            total = statistics.views.total,
            icon = Icons.Default.Visibility,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.height(8.dp))
        ModernLineChart(
            values = statistics.views.historical.values.map { it.value.toFloat() },
            labels = statistics.views.historical.values.map { it.date },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            primaryColor = MaterialTheme.colorScheme.secondary,
            noDataText = noDataText
        )
    }
}

@Composable
private fun StatHeader(
    title: String,
    total: Int,
    icon: ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatNumber(total.toFloat()),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ModernLineChart(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    noDataText: String = "No data",
) {
    if (values.isEmpty()) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text(text = noDataText, style = MaterialTheme.typography.labelMedium)
        }
        return
    }

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(values) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        )
    }

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    )

    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant

    val maxValue = values.maxOrNull() ?: 0f
    val minValue = 0f

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val paddingLeft = 40.dp.toPx()
        val paddingBottom = 20.dp.toPx()
        val paddingTop = 10.dp.toPx()
        val paddingRight = 10.dp.toPx()

        val chartWidth = width - paddingLeft - paddingRight
        val chartHeight = height - paddingBottom - paddingTop

        val stepX = chartWidth / (values.size - 1).coerceAtLeast(1)
        val valueRange = (maxValue - minValue).takeIf { it != 0f } ?: 1f

        val gridLines = 4
        for (i in 0..gridLines) {
            val y = chartHeight + paddingTop - (chartHeight / gridLines) * i
            val valueAtTick = minValue + (maxValue - minValue) * i / gridLines

            drawLine(
                color = outlineVariantColor.copy(alpha = 0.3f),
                start = Offset(paddingLeft, y),
                end = Offset(width - paddingRight, y),
                strokeWidth = 1.dp.toPx()
            )

            drawText(
                textMeasurer = textMeasurer,
                text = formatNumber(valueAtTick),
                style = labelStyle,
                topLeft = Offset(0f, y - 15f)
            )
        }

        val xLabelIndices = listOf(0, labels.size / 2, labels.size - 1)
        xLabelIndices.forEach { index ->
            if (index in labels.indices) {
                val x = paddingLeft + index * stepX
                val label = labels[index]
                
                val displayLabel = if (label.length >= 10) label.substring(5) else label

                drawText(
                    textMeasurer = textMeasurer,
                    text = displayLabel,
                    style = labelStyle,
                    topLeft = Offset(
                        x - (displayLabel.length * 8f),
                        chartHeight + paddingTop + 5f
                    )
                )
            }
        }

        val points = values.mapIndexed { index, value ->
            val x = paddingLeft + index * stepX
            val y = chartHeight + paddingTop - ((value - minValue) / valueRange) * chartHeight
            Offset(x, y)
        }

        if (points.size >= 2) {
            val strokePath = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    val prevPoint = points[i - 1]
                    val currPoint = points[i]
                    
                    val controlPoint1 = Offset(prevPoint.x + (currPoint.x - prevPoint.x) / 2f, prevPoint.y)
                    val controlPoint2 = Offset(prevPoint.x + (currPoint.x - prevPoint.x) / 2f, currPoint.y)
                    
                    cubicTo(
                        controlPoint1.x, controlPoint1.y,
                        controlPoint2.x, controlPoint2.y,
                        currPoint.x, currPoint.y
                    )
                }
            }

            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(points.last().x, chartHeight + paddingTop)
                lineTo(points.first().x, chartHeight + paddingTop)
                close()
            }

            clipRect(right = paddingLeft + (chartWidth * animationProgress.value)) {
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.3f),
                            primaryColor.copy(alpha = 0.0f)
                        ),
                        startY = paddingTop,
                        endY = chartHeight + paddingTop
                    )
                )

                drawPath(
                    path = strokePath,
                    color = primaryColor,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    }
}

private const val THOUSAND = 1_000f
private const val MILLION = 1_000_000f

fun formatNumber(value: Float): String {
    val absValue = abs(value)

    return when {
        absValue >= MILLION -> formatCompact(value / MILLION, "M")
        absValue >= THOUSAND -> formatCompact(value / THOUSAND, "k")
        else -> value.toInt().toString()
    }
}

private fun formatCompact(number: Float, suffix: String): String {
    return if (number % 1f == 0f) {
        number.toInt().toString() + suffix
    } else {
        String.format(Locale.getDefault(), "%.1f%s", number, suffix)
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
                        date = "2024-06-${(index + 1).toString().padStart(2, '0')}",
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
                        date = "2024-06-${(index + 1).toString().padStart(2, '0')}",
                        value = (30000..50000).random()
                    )
                }
            )
        )
    )

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            UserStatsChart(
                statistics = mockStats,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
