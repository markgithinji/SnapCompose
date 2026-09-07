package com.example.composegallery.core.database.local.search.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.composegallery.core.domain.model.RecentSearch

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    val timestamp: Long
)

fun RecentSearchEntity.toDomainModel(): RecentSearch {
    return RecentSearch(query = query, timestamp = timestamp)
}

fun RecentSearch.toEntity(): RecentSearchEntity {
    return RecentSearchEntity(query = query, timestamp = timestamp)
}
