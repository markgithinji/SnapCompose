package com.example.composegallery.feature.gallery.domain.model

data class UserStatistics(
    val username: String,
    val downloads: StatData,
    val views: StatData
)

data class StatData(
    val total: Int,
    val historical: HistoricalData
)

data class HistoricalData(
    val change: Int,
    val average: Int,
    val resolution: String,
    val quantity: Int,
    val values: List<StatValue>
)

data class StatValue(
    val date: String,
    val value: Int
)