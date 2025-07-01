package com.example.composegallery.feature.gallery.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import com.example.composegallery.feature.gallery.ui.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.composegallery.feature.gallery.data.util.Result


@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Photo>>(UiState.Loading)
    val uiState: StateFlow<UiState<Photo>> = _uiState

    val pagedPhotos: Flow<PagingData<Photo>> =
        galleryRepository.getPagedPhotos().cachedIn(viewModelScope)

    fun loadPhoto(photoId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = galleryRepository.getPhoto(photoId)) {
                is Result.Success -> _uiState.value = UiState.Content(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }
}

