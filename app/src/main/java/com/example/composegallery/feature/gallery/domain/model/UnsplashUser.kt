package com.example.composegallery.feature.gallery.domain.model

data class UnsplashUser(
    val id: String,
    val username: String,
    val name: String,
    val bio: String?,
    val location: String?,
    val profileImageSmall: String,
    val profileImageMedium: String,
    val profileImageLarge: String,
    val instagramUsername: String?,
    val totalPhotos: Int,
    val totalLikes: Int,
    val totalCollections: Int
)