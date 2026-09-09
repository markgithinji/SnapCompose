package com.example.composegallery.core.testing

import com.example.composegallery.core.database.local.search.dao.RecentSearchDao
import com.example.composegallery.core.database.local.search.entity.RecentSearchEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeRecentSearchDao : RecentSearchDao {

    private val searches = MutableStateFlow<Map<String, RecentSearchEntity>>(emptyMap())
    private var shouldThrow = false

    fun setShouldThrow(throwError: Boolean) {
        shouldThrow = throwError
    }

    override suspend fun insertSearch(search: RecentSearchEntity) {
        if (shouldThrow) throw RuntimeException("Fake DB error")
        searches.update { it + (search.query.lowercase() to search) }
    }

    override suspend fun deleteSearchIgnoreCase(query: String) {
        if (shouldThrow) throw RuntimeException("Fake DB error")
        searches.update { it - query.lowercase() }
    }

    override suspend fun deleteSearch(query: String) {
        if (shouldThrow) throw RuntimeException("Fake DB error")
        searches.update { it - query.lowercase() }
    }

    override suspend fun clearSearches() {
        if (shouldThrow) throw RuntimeException("Fake DB error")
        searches.value = emptyMap()
    }

    override fun getRecentSearches(limit: Int): Flow<List<RecentSearchEntity>> {
        return searches.map { it.values.toList().sortedByDescending { it.timestamp }.take(limit) }
    }
}
