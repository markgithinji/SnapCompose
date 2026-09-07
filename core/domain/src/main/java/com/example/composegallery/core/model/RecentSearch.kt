package com.example.composegallery.core.model

import kotlinx.serialization.Serializable

@Serializable
data class RecentSearch(
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)
