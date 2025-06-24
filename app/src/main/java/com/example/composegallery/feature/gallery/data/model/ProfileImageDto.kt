package com.example.composegallery.feature.gallery.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileImageDto(
    val small: String,
    val medium: String,
    val large: String
)