package com.example.composegallery.feature.gallery.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.data.local.RecentSearchDao
import com.example.composegallery.feature.gallery.data.pagingsource.PagingDefaults
import com.example.composegallery.feature.gallery.data.pagingsource.UnsplashSearchPagingSource
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.data.util.safeDbCall
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.RecentSearch
import com.example.composegallery.feature.gallery.domain.repository.SearchRepository
import com.example.composegallery.feature.gallery.util.StringProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultSearchRepository @Inject constructor(
    private val api: UnsplashApi,
    private val recentSearchDao: RecentSearchDao,
    private val stringProvider: StringProvider
) : SearchRepository {

    override fun searchPagedPhotos(query: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingDefaults.PAGE_SIZE,
                initialLoadSize = PagingDefaults.INITIAL_LOAD_SIZE,
            ),
            pagingSourceFactory = { UnsplashSearchPagingSource(api, query, stringProvider) }
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

    override suspend fun clearRecentSearches(): Result<Unit> {
        return safeDbCall(stringProvider) {
            recentSearchDao.clearSearches()
        }
    }
}
