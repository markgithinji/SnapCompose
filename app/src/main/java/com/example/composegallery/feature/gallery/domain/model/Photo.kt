package com.example.composegallery.feature.gallery.domain.model

data class Photo(
    val id: String,
    val width: Int,
    val height: Int,
    val thumbUrl: String,
    val fullUrl: String,
    val authorName: String,
    val authorProfileImageUrl: String
)