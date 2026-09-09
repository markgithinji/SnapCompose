package com.example.composegallery.feature.search.ui

import com.example.composegallery.core.domain.model.ColorFilter
import com.example.composegallery.core.domain.model.Orientation
import com.example.composegallery.core.domain.model.OrderBy
import com.example.composegallery.core.domain.model.SearchFilters
import com.example.composegallery.feature.search.data.repository.FakeSearchRepository
import com.example.composegallery.feature.search.domain.usecase.ObserveSearchResultsUseCase
import com.example.composegallery.feature.search.domain.usecase.SubmitSearchUseCase
import com.example.composegallery.feature.search.fakes.FakeNetworkMonitor
import com.example.composegallery.core.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val searchRepository = FakeSearchRepository()
    private val observeSearchResults = ObserveSearchResultsUseCase(searchRepository)
    private val submitSearchUseCase = SubmitSearchUseCase(searchRepository)
    private val networkMonitor = FakeNetworkMonitor()

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        viewModel = SearchViewModel(
            observeSearchResults,
            submitSearchUseCase,
            searchRepository,
            networkMonitor
        )
    }

    @Test
    fun updateQuery_updatesSearchQuery() {
        val newQuery = "cats"
        viewModel.updateQuery(newQuery)
        assertThat(viewModel.searchQuery.value).isEqualTo(newQuery)
    }

    @Test
    fun updateQuery_empty_clearsFilters() {
        viewModel.submitSearch("cats")
        viewModel.updateQuery("")
        assertThat(viewModel.filters.value.query).isEmpty()
    }

    @Test
    fun updateOrientation_updatesFilters() {
        viewModel.updateOrientation(Orientation.LANDSCAPE)
        assertThat(viewModel.filters.value.orientation).isEqualTo(Orientation.LANDSCAPE)
    }

    @Test
    fun updateColor_updatesFilters() {
        viewModel.updateColor(ColorFilter.BLACK_AND_WHITE)
        assertThat(viewModel.filters.value.color).isEqualTo(ColorFilter.BLACK_AND_WHITE)
    }

    @Test
    fun updateOrderBy_updatesFilters() {
        viewModel.updateOrderBy(OrderBy.LATEST)
        assertThat(viewModel.filters.value.orderBy).isEqualTo(OrderBy.LATEST)
    }

    @Test
    fun applyFilters_updatesAllFilters() {
        val newFilters = SearchFilters(query = "cats", orientation = Orientation.PORTRAIT)
        viewModel.applyFilters(newFilters)
        assertThat(viewModel.filters.value).isEqualTo(newFilters)
    }

    @Test
    fun submitSearch_updatesFiltersAndSavesRecentSearch() = runTest {
        val query = "nature"

        viewModel.submitSearch(query)

        assertThat(viewModel.filters.value.query).isEqualTo(query)
        
        val recentSearches = searchRepository.getRecentSearches(10).first()
        assertThat(recentSearches.map { it.query }).contains(query)
    }

    @Test
    fun clearRecentSearches_clearsRepository() = runTest {
        searchRepository.saveRecentSearch("cats")
        
        viewModel.clearRecentSearches()

        val recentSearches = searchRepository.getRecentSearches(10).first()
        assertThat(recentSearches).isEmpty()
    }
}
