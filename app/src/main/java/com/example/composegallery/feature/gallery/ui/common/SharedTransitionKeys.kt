package com.example.composegallery.feature.gallery.ui.common

object SharedTransitionKeys {
    const val SEARCH_BAR = "searchBarElement"

    fun photoImage(id: String, origin: String = "gallery") = "photo_${origin}_$id"
}
