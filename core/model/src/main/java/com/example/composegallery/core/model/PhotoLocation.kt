package com.example.composegallery.core.model

import kotlinx.serialization.Serializable

@Serializable
data class PhotoLocation(
    val city: String?,
    val country: String?
)
