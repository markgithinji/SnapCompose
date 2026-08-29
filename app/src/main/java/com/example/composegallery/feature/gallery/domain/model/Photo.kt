package com.example.composegallery.feature.gallery.domain.model

data class Photo(
    val id: String,
    val width: Int,
    val height: Int,
    val thumbUrl: String,
    val smallUrl: String,
    val regularUrl: String,
    val fullUrl: String,
    val authorName: String,
    val authorProfileImageUrl: String,
    val authorProfileImageMediumResUrl: String,
    val authorProfileImageHighResUrl: String,
    val authorUnsplashUrl: String?,
    val username: String?,
    val downloadLocationUrl: String?,
    val location: PhotoLocation?,
    val blurHash: String? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val exif: Exif? = null,
    val debugOrder: Int = -1
)

