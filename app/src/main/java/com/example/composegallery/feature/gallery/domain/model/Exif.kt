package com.example.composegallery.feature.gallery.domain.model

data class Exif(
    val make: String?,
    val model: String?,
    val aperture: String?,
    val shutterSpeed: String?,
    val focalLength: String?,
    val iso: Int?
)