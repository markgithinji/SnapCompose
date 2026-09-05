package com.example.composegallery.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UrlsDto(
    val thumb: String,
    val small: String,
    val regular: String,
    val full: String
)
