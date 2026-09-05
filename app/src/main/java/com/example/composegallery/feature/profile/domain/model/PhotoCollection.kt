package com.example.composegallery.feature.profile.domain.model

import com.example.composegallery.core.model.Photo

data class PhotoCollection(
    val id: String,
    val title: String,
    val description: String?,
    val totalPhotos: Int,
    val coverPhoto: Photo?,
    val authorName: String
)
