package com.example.composegallery.feature.search.data.repository

import com.example.composegallery.core.common.Result
import com.example.composegallery.core.network.remote.UnsplashApi
import com.example.composegallery.core.common.StringProvider
import com.example.composegallery.core.database.local.search.dao.RecentSearchDao
import com.example.composegallery.core.database.local.search.entity.RecentSearchEntity
import com.example.composegallery.core.domain.model.RecentSearch
import com.example.composegallery.feature.search.data.DefaultSearchRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DefaultSearchRepositoryTest {

    private lateinit var api: UnsplashApi
    private lateinit var dao: RecentSearchDao
    private lateinit var stringProvider: StringProvider
    private lateinit var repository: DefaultSearchRepository

    @Before
    fun setup() {
        api = mock()
        dao = mock()
        stringProvider = mock()
        repository = DefaultSearchRepository(api, dao, stringProvider)
    }

    @Test
    fun savesRecentSearch_afterDeletingOldEntry() = runTest {
        val query = "mountains"
        whenever(runBlocking { dao.deleteSearchIgnoreCase(query) }).thenReturn(Unit)
        whenever(runBlocking { dao.insertSearch(any()) }).thenReturn(Unit)

        val result = repository.saveRecentSearch(query)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        runBlocking { verify(dao).deleteSearchIgnoreCase(query) }
        runBlocking { verify(dao).insertSearch(argThat { this.query == query }) }
    }

    @Test
    fun returnsError_whenSaveRecentSearchFailsDueToDbError() = runTest {
        val query = "crash"
        whenever(runBlocking { dao.deleteSearchIgnoreCase(query) }).thenThrow(RuntimeException("DB error"))
        whenever(stringProvider.get(any())).thenReturn("db failure")

        val result = repository.saveRecentSearch(query)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("db failure")
    }

    @Test
    fun clearsAllRecentSearchesSuccessfully() = runTest {
        whenever(runBlocking { dao.clearSearches() }).thenReturn(Unit)

        val result = repository.clearRecentSearches()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        runBlocking { verify(dao).clearSearches() }
    }

    @Test
    fun returnsError_whenClearRecentSearchesFails() = runTest {
        whenever(runBlocking { dao.clearSearches() }).thenThrow(RuntimeException("boom"))
        whenever(stringProvider.get(any())).thenReturn("something went wrong")

        val result = repository.clearRecentSearches()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("something went wrong")
    }

    @Test
    fun emitsRecentSearchListFromDao() = runTest {
        val limit = 5
        val expectedEntities = listOf(
            RecentSearchEntity("one", 100),
            RecentSearchEntity("two", 200)
        )
        whenever(dao.getRecentSearches(limit)).thenReturn(flowOf(expectedEntities))

        val flow = repository.getRecentSearches(limit)
        val result = flow.first()

        assertThat(result).hasSize(2)
        assertThat(result[0].query).isEqualTo("one")
        assertThat(result[1].query).isEqualTo("two")
        verify(dao).getRecentSearches(limit)
    }
}
