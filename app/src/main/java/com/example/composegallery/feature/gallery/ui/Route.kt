package com.example.composegallery.feature.gallery.ui

import kotlinx.serialization.Serializable

@Serializable
object Gallery

@Serializable
object Search

@Serializable
data class PhotoDetail(val photoId: String)