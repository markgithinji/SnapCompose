package com.example.composegallery.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val total: Int,
    @SerialName("total_pages") val totalPages: Int,
    val results: List<UnsplashPhotoDto>
)
