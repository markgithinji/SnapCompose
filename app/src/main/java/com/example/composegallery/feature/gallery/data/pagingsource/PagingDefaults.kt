package com.example.composegallery.feature.gallery.data.pagingsource

object PagingDefaults {
    const val PAGE_SIZE = 30
    const val INITIAL_LOAD_SIZE = 90 // Load 3 pages initially to avoid jumps on invalidation
    const val PREFETCH_DISTANCE = PAGE_SIZE
}
