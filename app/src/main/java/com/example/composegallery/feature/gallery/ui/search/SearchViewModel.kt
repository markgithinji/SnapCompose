package com.example.composegallery.feature.gallery.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.RecentSearch
import com.example.composegallery.feature.gallery.domain.repository.SearchRepository
import com.example.composegallery.feature.gallery.domain.usecase.ObserveSearchResultsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    observeSearchResults: ObserveSearchResultsUseCase,
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val searchResults: StateFlow<PagingData<Photo>> =
        observeSearchResults(query)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PagingData.empty())

    val recentSearches: StateFlow<List<RecentSearch>> =
        searchRepository.getRecentSearches(limit = 10)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun submitSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        _query.value = trimmed
        saveSearchQuery(trimmed)
    }

    private fun saveSearchQuery(query: String) {
        viewModelScope.launch {
            searchRepository.saveRecentSearch(query)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            searchRepository.clearRecentSearches()
        }
    }

    // fun deleteRecentSearch(query: String) {
    //     viewModelScope.launch {
    //         searchRepository.deleteRecentSearch(query)
    //     }
    // }
}

