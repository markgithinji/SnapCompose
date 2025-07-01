package com.example.composegallery.feature.gallery.domain.repository

import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.RecentSearch
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchPagedPhotos(query: String): Flow<PagingData<Photo>>
    fun getRecentSearches(limit: Int): Flow<List<RecentSearch>>
    suspend fun saveRecentSearch(query: String)
    suspend fun clearRecentSearches()
}