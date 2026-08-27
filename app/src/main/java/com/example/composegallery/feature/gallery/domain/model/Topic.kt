package com.example.composegallery.feature.gallery.domain.model

data class Topic(
    val id: String,
    val slug: String,
    val title: String,
    val description: String?,
    val totalPhotos: Int
)
