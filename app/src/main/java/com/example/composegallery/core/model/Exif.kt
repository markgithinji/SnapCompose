package com.example.composegallery.core.model

data class Exif(
    val make: String?,
    val model: String?,
    val aperture: String?,
    val shutterSpeed: String?,
    val focalLength: String?,
    val iso: Int?
)
