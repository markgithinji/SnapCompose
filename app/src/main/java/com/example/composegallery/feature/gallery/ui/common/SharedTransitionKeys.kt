package com.example.composegallery.feature.gallery.ui.common

object SharedTransitionKeys {
    const val SEARCH_BAR = "searchBarElement"

    fun photoImage(id: String) = "photo_image_$id"

    fun userProfileImage(username: String) = "user_profile_image_$username"

    fun collectionImage(id: String) = "collection_image_$id"
}
