package com.example.composegallery.feature.gallery.data.model

import com.example.composegallery.feature.gallery.domain.model.HistoricalData
import com.example.composegallery.feature.gallery.domain.model.StatData
import com.example.composegallery.feature.gallery.domain.model.StatValue
import com.example.composegallery.feature.gallery.domain.model.UserStatistics
import kotlinx.serialization.Serializable

@Serializable
data class UserStatisticsDto(
    val username: String,
    val downloads: StatsDto,
    val views: StatsDto
)

@Serializable
data class StatsDto(
    val total: Int,
    val historical: HistoricalDto
)

@Serializable
data class HistoricalDto(
    val change: Int,
    val average: Int,
    val resolution: String,
    val quantity: Int,
    val values: List<StatValueDto>
)

@Serializable
data class StatValueDto(
    val date: String,
    val value: Int
)

fun UserStatisticsDto.toDomain(): UserStatistics = UserStatistics(
    username = this.username,
    downloads = StatData(
        total = this.downloads.total,
        historical = HistoricalData(
            change = this.downloads.historical.change,
            average = this.downloads.historical.average,
            resolution = this.downloads.historical.resolution,
            quantity = this.downloads.historical.quantity,
            values = this.downloads.historical.values.map {
                StatValue(date = it.date, value = it.value)
            }
        )
    ),
    views = StatData(
        total = this.views.total,
        historical = HistoricalData(
            change = this.views.historical.change,
            average = this.views.historical.average,
            resolution = this.views.historical.resolution,
            quantity = this.views.historical.quantity,
            values = this.views.historical.values.map {
                StatValue(date = it.date, value = it.value)
            }
        )
    )
)

