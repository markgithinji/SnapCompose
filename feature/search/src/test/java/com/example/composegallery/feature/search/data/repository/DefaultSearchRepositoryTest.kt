package com.example.composegallery.feature.search.data.repository

import com.example.composegallery.core.common.Result
import com.example.composegallery.core.database.local.search.entity.RecentSearchEntity
import com.example.composegallery.feature.search.data.DefaultSearchRepository
import com.example.composegallery.feature.search.fakes.FakeRecentSearchDao
import com.example.composegallery.feature.search.fakes.FakeStringProvider
import com.example.composegallery.feature.search.fakes.FakeUnsplashApi
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultSearchRepositoryTest {

    private val api = FakeUnsplashApi()
    private val dao = FakeRecentSearchDao()
    private val stringProvider = FakeStringProvider()
    private lateinit var repository: DefaultSearchRepository

    @Before
    fun setup() {
        repository = DefaultSearchRepository(api, dao, stringProvider)
    }

    @Test
    fun savesRecentSearch_afterDeletingOldEntry() = runTest {
        val query = "mountains"

        val result = repository.saveRecentSearch(query)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val recentSearches = dao.getRecentSearches(10).first()
        assertThat(recentSearches.map { it.query }).contains(query)
    }

    @Test
    fun returnsError_whenSaveRecentSearchFailsDueToDbError() = runTest {
        val query = "crash"
        dao.setShouldThrow(true)

        val result = repository.saveRecentSearch(query)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("fake")
    }

    @Test
    fun clearsAllRecentSearchesSuccessfully() = runTest {
        dao.insertSearch(RecentSearchEntity("cats", 123))

        val result = repository.clearRecentSearches()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(dao.getRecentSearches(10).first()).isEmpty()
    }

    @Test
    fun returnsError_whenClearRecentSearchesFails() = runTest {
        dao.setShouldThrow(true)

        val result = repository.clearRecentSearches()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun emitsRecentSearchListFromDao() = runTest {
        val limit = 5
        dao.insertSearch(RecentSearchEntity("one", 100))
        dao.insertSearch(RecentSearchEntity("two", 200))

        val flow = repository.getRecentSearches(limit)
        val result = flow.first()

        assertThat(result).hasSize(2)
        assertThat(result[0].query).isEqualTo("two") // Sorted by timestamp desc
        assertThat(result[1].query).isEqualTo("one")
    }
}
