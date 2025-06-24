package com.example.composegallery.feature.gallery.domain.model

data class Photo(
    val id: String,
    val imageUrl: String,
    val authorName: String,
    val likes: Int
)