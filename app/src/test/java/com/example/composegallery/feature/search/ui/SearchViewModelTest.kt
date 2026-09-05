package com.example.composegallery.feature.search.ui

import com.example.composegallery.core.data.Result
import com.example.composegallery.feature.search.domain.model.ColorFilter
import com.example.composegallery.feature.search.domain.model.Orientation
import com.example.composegallery.feature.search.domain.model.OrderBy
import com.example.composegallery.feature.search.domain.model.SearchFilters
import com.example.composegallery.feature.search.domain.repository.SearchRepository
import com.example.composegallery.feature.search.domain.usecase.ObserveSearchResultsUseCase
import com.example.composegallery.feature.search.domain.usecase.SubmitSearchUseCase
import com.example.composegallery.utils.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeSearchResults: ObserveSearchResultsUseCase = mock()
    private val submitSearchUseCase: SubmitSearchUseCase = mock()
    private val searchRepository: SearchRepository = mock()

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        whenever(observeSearchResults(any())).thenReturn(flowOf())
        whenever(searchRepository.getRecentSearches(any())).thenReturn(flowOf(emptyList()))
        
        viewModel = SearchViewModel(
            observeSearchResults,
            submitSearchUseCase,
            searchRepository
        )
    }

    @Test
    fun updateQuery_updatesFilters() {
        val newQuery = "cats"
        viewModel.updateQuery(newQuery)
        assertThat(viewModel.filters.value.query).isEqualTo(newQuery)
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
    fun submitSearch_callsUseCase() = runTest {
        val query = "nature"
        whenever(submitSearchUseCase(query)).thenReturn(Result.Success(query))

        viewModel.submitSearch(query)

        verify(submitSearchUseCase).invoke(query)
    }

    @Test
    fun clearRecentSearches_callsRepository() = runTest {
        whenever(searchRepository.clearRecentSearches()).thenReturn(Result.Success(Unit))

        viewModel.clearRecentSearches()

        verify(searchRepository).clearRecentSearches()
    }
}
