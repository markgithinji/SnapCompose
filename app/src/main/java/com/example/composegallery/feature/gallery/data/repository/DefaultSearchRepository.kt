package com.example.composegallery.feature.gallery.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.data.local.RecentSearchDao
import com.example.composegallery.feature.gallery.data.pagingsource.UnsplashSearchPagingSource
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.RecentSearch
import com.example.composegallery.feature.gallery.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultSearchRepository @Inject constructor(
    private val api: UnsplashApi,
    private val recentSearchDao: RecentSearchDao
) : SearchRepository {

    override fun searchPagedPhotos(query: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { UnsplashSearchPagingSource(api, query) }
        ).flow
    }

    override fun getRecentSearches(limit: Int): Flow<List<RecentSearch>> {
        return recentSearchDao.getRecentSearches(limit)
    }

    override suspend fun saveRecentSearch(query: String) {
        if (query.isNotBlank()) {
            recentSearchDao.insertSearch(RecentSearch(query = query.trim()))
        }
    }

    override suspend fun clearRecentSearches() {
        recentSearchDao.clearSearches()
    }
}