package com.example.composegallery.feature.gallery.ui.search

import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.repository.SearchRepository
import com.example.composegallery.feature.gallery.domain.usecase.ObserveSearchResultsUseCase
import com.example.composegallery.feature.gallery.domain.usecase.SubmitSearchUseCase
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
