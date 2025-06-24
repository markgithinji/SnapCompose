package com.example.composegallery.feature.gallery.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UrlsDto(
    val small: String,
    val regular: String
)
