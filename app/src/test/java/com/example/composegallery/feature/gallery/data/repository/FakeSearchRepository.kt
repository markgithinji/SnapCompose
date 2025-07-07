package com.example.composegallery.feature.gallery.data.repository

import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.RecentSearch
import com.example.composegallery.feature.gallery.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

open class FakeSearchRepository : SearchRepository {

    private val fakeResults = mutableMapOf<String, List<Photo>>()
    private val recentSearchesFlow = MutableStateFlow<List<RecentSearch>>(emptyList())

    override fun searchPagedPhotos(query: String): Flow<PagingData<Photo>> {
        val photos = fakeResults[query] ?: emptyList()
        return flow {
            emit(PagingData.from(photos))
        }
    }

    override fun getRecentSearches(limit: Int): Flow<List<RecentSearch>> {
        return recentSearchesFlow.map { it.take(limit) }
    }

    override suspend fun saveRecentSearch(query: String): Result<Unit> {
        val updatedList = recentSearchesFlow.value.toMutableList()
            .filterNot { it.query.equals(query, ignoreCase = true) }
            .toMutableList()

        updatedList.add(0, RecentSearch(query))
        recentSearchesFlow.update { updatedList }

        return Result.Success(Unit)
    }

    override suspend fun clearRecentSearches(): Result<Unit> {
        recentSearchesFlow.value = emptyList()
        return Result.Success(Unit)
    }
}
