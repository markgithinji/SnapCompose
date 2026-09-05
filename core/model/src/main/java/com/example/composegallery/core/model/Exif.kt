package com.example.composegallery.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Exif(
    val make: String?,
    val model: String?,
    val aperture: String?,
    val shutterSpeed: String?,
    val focalLength: String?,
    val iso: Int?
)
