package com.example.composegallery.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RecentSearch(
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)
