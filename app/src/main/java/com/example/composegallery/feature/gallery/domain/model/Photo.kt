package com.example.composegallery.feature.gallery.domain.model

data class Photo(
    val id: String,
    val thumbUrl: String,    // low-res: good for grid previews
    val fullUrl: String,   // full-res: good for detail view
    val authorName: String
)