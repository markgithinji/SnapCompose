package com.example.composegallery.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.composegallery.core.model.Result
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.model.ColorFilter
import com.example.composegallery.core.model.OrderBy
import com.example.composegallery.core.model.Orientation
import com.example.composegallery.core.model.RecentSearch
import com.example.composegallery.core.model.SearchFilters
import com.example.composegallery.feature.search.domain.repository.SearchRepository
import com.example.composegallery.feature.search.domain.usecase.ObserveSearchResultsUseCase
import com.example.composegallery.feature.search.domain.usecase.SubmitSearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    observeSearchResults: ObserveSearchResultsUseCase,
    private val submitSearchUseCase: SubmitSearchUseCase,
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _filters = MutableStateFlow(SearchFilters())
    val filters: StateFlow<SearchFilters> = _filters.asStateFlow()

    val searchResults: Flow<PagingData<Photo>> =
        observeSearchResults(filters).cachedIn(viewModelScope)

    val recentSearches: StateFlow<List<RecentSearch>> =
        searchRepository.getRecentSearches(limit = 10)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateQuery(query: String) {
        _filters.update { it.copy(query = query) }
    }

    fun updateOrientation(orientation: Orientation?) {
        _filters.update { it.copy(orientation = orientation) }
    }

    fun updateColor(color: ColorFilter?) {
        _filters.update { it.copy(color = color) }
    }

    fun updateOrderBy(orderBy: OrderBy) {
        _filters.update { it.copy(orderBy = orderBy) }
    }

    fun applyFilters(newFilters: SearchFilters) {
        _filters.value = newFilters
    }

    fun submitSearch(query: String) {
        viewModelScope.launch {
            when (val result = submitSearchUseCase(query)) {
                is Result.Success<*> -> updateQuery(result.data as String)
                is Result.Error -> {
                    Timber.w("Search submission failed: ${result.message}")
                }
            }
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            searchRepository.clearRecentSearches()
        }
    }

    fun deleteRecentSearch(query: String) {
        viewModelScope.launch {
            searchRepository.deleteRecentSearch(query)
        }
    }
}
