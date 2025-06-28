package com.example.composegallery.feature.gallery.ui

import kotlinx.serialization.Serializable

@Serializable
object GalleryRoute

@Serializable
object SearchRoute

@Serializable
data class PhotoDetailRoute(val photoId: String)

@Serializable
data class FullscreenPhotoRoute(val photoId: String)