package com.example.composegallery.feature.gallery.domain.model

data class PhotoCollection(
    val id: String,
    val title: String,
    val description: String?,
    val totalPhotos: Int,
    val coverPhoto: Photo?,
    val authorName: String
)