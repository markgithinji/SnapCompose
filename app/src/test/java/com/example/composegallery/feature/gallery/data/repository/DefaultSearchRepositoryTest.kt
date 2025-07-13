package com.example.composegallery.feature.gallery.data.repository

import com.example.composegallery.feature.gallery.data.local.RecentSearchDao
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.RecentSearch
import com.example.composegallery.feature.gallery.util.StringProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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
        whenever(dao.deleteSearchIgnoreCase(query)).thenReturn(Unit)
        whenever(dao.insertSearch(any())).thenReturn(Unit)

        val result = repository.saveRecentSearch(query)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        verify(dao).deleteSearchIgnoreCase(query)
        verify(dao).insertSearch(argThat { this.query == query })
    }

    @Test
    fun returnsError_whenSaveRecentSearchFailsDueToDbError() = runTest {
        val query = "crash"
        whenever(dao.deleteSearchIgnoreCase(query)).thenThrow(RuntimeException("DB error"))
        whenever(stringProvider.get(any())).thenReturn("db failure")

        val result = repository.saveRecentSearch(query)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("db failure")
    }

    @Test
    fun clearsAllRecentSearchesSuccessfully() = runTest {
        whenever(dao.clearSearches()).thenReturn(Unit)

        val result = repository.clearRecentSearches()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        verify(dao).clearSearches()
    }

    @Test
    fun returnsError_whenClearRecentSearchesFails() = runTest {
        whenever(dao.clearSearches()).thenThrow(RuntimeException("boom"))
        whenever(stringProvider.get(any())).thenReturn("something went wrong")

        val result = repository.clearRecentSearches()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("something went wrong")
    }

    @Test
    fun emitsRecentSearchListFromDao() = runTest {
        val limit = 5
        val expectedSearches = listOf(
            RecentSearch("one"),
            RecentSearch("two")
        )
        whenever(dao.getRecentSearches(limit)).thenReturn(flowOf(expectedSearches))

        val flow = repository.getRecentSearches(limit)
        val result = flow.first()

        assertThat(result).hasSize(2)
        assertThat(result[0].query).isEqualTo("one")
        assertThat(result[1].query).isEqualTo("two")
        verify(dao).getRecentSearches(limit)
    }
}
