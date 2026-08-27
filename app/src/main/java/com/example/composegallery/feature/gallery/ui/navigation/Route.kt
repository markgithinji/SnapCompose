package com.example.composegallery.feature.gallery.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object GalleryRoute

@Serializable
object SearchRoute

@Serializable
data class PhotoDetailRoute(
    val photoId: String,
    val width: Int,
    val height: Int,
    val thumbUrl: String? = null,
    val blurHash: String? = null,
    val origin: String = "gallery"
)

@Serializable
data class FullscreenPhotoRoute(val photoId: String)

@Serializable
data class UserProfileRoute(
    val username: String,
    val name: String? = null,
    val profileImageUrl: String? = null
)

@Serializable
data class CollectionDetailRoute(
    val collectionId: String,
    val collectionTitle: String,
    val totalPhotos: Int
)
