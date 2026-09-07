package com.example.composegallery.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PhotoLocation(
    val city: String?,
    val country: String?
)
