package com.example.composegallery.feature.gallery.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topic_cache_metadata")
data class TopicCacheMetadataEntity(
    @PrimaryKey val topicId: String,
    val lastUpdated: Long
)
