package com.example.composegallery.core.ui

object SharedTransitionKeys {
    const val SEARCH_BAR = "searchBarElement"

    fun photoImage(id: String, origin: String = "gallery") = "photo_${origin}_$id"
}
