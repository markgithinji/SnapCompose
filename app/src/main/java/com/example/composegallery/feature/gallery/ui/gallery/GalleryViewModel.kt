package com.example.composegallery.feature.gallery.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.composegallery.feature.gallery.domain.model.ObserveSearchResultsUseCase
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class GalleryViewModel @Inject constructor(
    galleryRepository: GalleryRepository,
    observeSearchResults: ObserveSearchResultsUseCase
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val pagedPhotos: Flow<PagingData<Photo>> =
        galleryRepository.getPagedPhotos().cachedIn(viewModelScope)

    val searchResults: Flow<PagingData<Photo>> =
        observeSearchResults(_searchQuery)
            .cachedIn(viewModelScope)

    fun submitSearch(query: String) {
        _searchQuery.value = query.trim()
    }
}

