package com.example.composegallery.core.domain.repository

import androidx.paging.PagingData
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.domain.model.RecentSearch
import com.example.composegallery.core.common.Result
import com.example.composegallery.core.domain.model.SearchFilters
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchPagedPhotos(filters: SearchFilters): Flow<PagingData<Photo>>
    fun getRecentSearches(limit: Int): Flow<List<RecentSearch>>
    suspend fun saveRecentSearch(query: String): Result<Unit>
    suspend fun deleteRecentSearch(query: String): Result<Unit>
    suspend fun clearRecentSearches(): Result<Unit>
}
