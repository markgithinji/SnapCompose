package com.example.composegallery.feature.gallery.domain.model

data class Collection(
    val id: String,
    val title: String,
    val description: String?,
    val totalPhotos: Int,
    val coverPhoto: Photo?,
    val authorName: String
)