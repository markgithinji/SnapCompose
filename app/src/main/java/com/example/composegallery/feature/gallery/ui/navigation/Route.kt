package com.example.composegallery.feature.gallery.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object GalleryRoute

@Serializable
object SearchRoute

@Serializable
data class PhotoDetailRoute(val photoId: String)

@Serializable
data class FullscreenPhotoRoute(val photoId: String)

@Serializable
data class UserProfileRoute(val username: String)

@Serializable
data class CollectionDetailRoute(val collectionId: String, val collectionTitle: String)