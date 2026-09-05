package com.example.composegallery.feature.search.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.composegallery.core.data.Result
import com.example.composegallery.core.data.paging.PagingDefaults
import com.example.composegallery.core.data.safeDbCall
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.data.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider
import com.example.composegallery.feature.search.data.local.RecentSearchDao
import com.example.composegallery.feature.search.data.paging.UnsplashSearchPagingSource
import com.example.composegallery.feature.search.domain.model.RecentSearch
import com.example.composegallery.feature.search.domain.model.SearchFilters
import com.example.composegallery.feature.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultSearchRepository @Inject constructor(
    private val api: UnsplashApi,
    private val recentSearchDao: RecentSearchDao,
    private val stringProvider: StringProvider
) : SearchRepository {

    override fun searchPagedPhotos(filters: SearchFilters): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingDefaults.PAGE_SIZE,
                initialLoadSize = PagingDefaults.INITIAL_LOAD_SIZE,
            ),
            pagingSourceFactory = { UnsplashSearchPagingSource(api, filters, stringProvider) }
        ).flow
    }

    override fun getRecentSearches(limit: Int): Flow<List<RecentSearch>> {
        return recentSearchDao.getRecentSearches(limit)
    }

    override suspend fun saveRecentSearch(query: String): Result<Unit> {
        return safeDbCall(stringProvider) {
            recentSearchDao.deleteSearchIgnoreCase(query)
            recentSearchDao.insertSearch(RecentSearch(query = query))
        }
    }

    override suspend fun deleteRecentSearch(query: String): Result<Unit> {
        return safeDbCall(stringProvider) {
            recentSearchDao.deleteSearch(query)
        }
    }

    override suspend fun clearRecentSearches(): Result<Unit> {
        return safeDbCall(stringProvider) {
            recentSearchDao.clearSearches()
        }
    }
}
