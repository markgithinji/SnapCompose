package com.example.composegallery.feature.gallery.ui

import android.net.Uri

sealed class Screen(val route: String) {
    object Gallery : Screen("gallery")
    object SearchResults : Screen("search?query={query}") {
        fun createRoute(query: String) = "search?query=${Uri.encode(query)}"
    }
}