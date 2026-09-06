package com.example.composegallery.feature.search.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.composegallery.core.common.Result
import com.example.composegallery.core.network.paging.PagingDefaults
import com.example.composegallery.core.common.safeDbCall
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.network.remote.UnsplashApi
import com.example.composegallery.core.common.StringProvider
import com.example.composegallery.core.database.local.search.dao.RecentSearchDao
import com.example.composegallery.core.model.RecentSearch
import com.example.composegallery.core.model.SearchFilters
import com.example.composegallery.feature.search.domain.repository.SearchRepository
import com.example.composegallery.core.database.local.search.entity.toDomainModel
import com.example.composegallery.core.database.local.search.entity.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
        return recentSearchDao.getRecentSearches(limit).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun saveRecentSearch(query: String): Result<Unit> {
        return safeDbCall(stringProvider) {
            recentSearchDao.deleteSearchIgnoreCase(query)
            recentSearchDao.insertSearch(RecentSearch(query = query).toEntity())
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
