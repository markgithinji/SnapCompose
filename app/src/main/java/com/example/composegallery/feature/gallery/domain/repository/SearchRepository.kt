package com.example.composegallery.feature.gallery.domain.repository

import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.RecentSearch
import kotlinx.coroutines.flow.Flow

import com.example.composegallery.feature.gallery.domain.model.SearchFilters

interface SearchRepository {
    fun searchPagedPhotos(filters: SearchFilters): Flow<PagingData<Photo>>
    fun getRecentSearches(limit: Int): Flow<List<RecentSearch>>
    suspend fun saveRecentSearch(query: String): Result<Unit>
    suspend fun deleteRecentSearch(query: String): Result<Unit>
    suspend fun clearRecentSearches(): Result<Unit>
}
