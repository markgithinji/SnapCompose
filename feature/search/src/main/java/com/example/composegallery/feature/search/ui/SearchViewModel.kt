package com.example.composegallery.feature.search.ui

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.composegallery.core.common.Result
import com.example.composegallery.core.common.network.NetworkMonitor
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.domain.model.ColorFilter
import com.example.composegallery.core.domain.model.OrderBy
import com.example.composegallery.core.domain.model.Orientation
import com.example.composegallery.core.domain.model.RecentSearch
import com.example.composegallery.core.domain.model.SearchFilters
import com.example.composegallery.core.domain.repository.SearchRepository
import com.example.composegallery.feature.search.domain.usecase.ObserveSearchResultsUseCase
import com.example.composegallery.feature.search.domain.usecase.SubmitSearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    observeSearchResults: ObserveSearchResultsUseCase,
    private val submitSearchUseCase: SubmitSearchUseCase,
    private val searchRepository: SearchRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<SearchUiEvent>()
    val uiEvent: SharedFlow<SearchUiEvent> = _uiEvent.asSharedFlow()

    private val _filters = MutableStateFlow(SearchFilters())
    val filters: StateFlow<SearchFilters> = _filters.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val searchResults: Flow<PagingData<Photo>> =
        observeSearchResults(filters)
            .onEach { _isSearching.value = false }
            .cachedIn(viewModelScope)

    val recentSearches: StateFlow<List<RecentSearch>> =
        searchRepository.getRecentSearches(limit = 10)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateQuery(query: String) {
        _searchQuery.value = query
        // If the query is cleared, immediately clear results as well
        if (query.isBlank()) {
            _filters.update { it.copy(query = "") }
        }
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
        _searchQuery.value = newFilters.query
        _filters.value = newFilters
    }

    fun submitSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        
        _isSearching.value = true
        _searchQuery.value = trimmed
        _filters.update { it.copy(query = trimmed) }

        viewModelScope.launch {
            when (val result = submitSearchUseCase(trimmed)) {
                is Result.Success<*> -> { /* History saved */ }
                is Result.Error -> {
                    _uiEvent.emit(SearchUiEvent.ShowSnackbar(result.message))
                }
            }
            _isSearching.value = false
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

sealed class SearchUiEvent {
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val duration: SnackbarDuration = SnackbarDuration.Short
    ) : SearchUiEvent()
}