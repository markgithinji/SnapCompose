package com.example.composegallery.feature.gallery.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import com.example.composegallery.feature.gallery.domain.repository.PhotoActionsRepository
import com.example.composegallery.feature.gallery.ui.util.UiState
import com.example.composegallery.feature.gallery.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    private val photoActionsRepository: PhotoActionsRepository,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Photo>>(UiState.Loading)
    val uiState: StateFlow<UiState<Photo>> = _uiState

    private val _actionEvent = MutableSharedFlow<String>()
    val actionEvent: SharedFlow<String> = _actionEvent.asSharedFlow()

    private val _isActionLoading = MutableStateFlow(false)
    val isActionLoading: StateFlow<Boolean> = _isActionLoading

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

    fun reportDownload(downloadUrl: String) {
        viewModelScope.launch {
            galleryRepository.reportDownload(downloadUrl)
        }
    }

    fun downloadPhoto(photo: Photo) {
        viewModelScope.launch {
            val fileName = "Snap_${photo.id}.jpg"
            when (val result = photoActionsRepository.downloadPhoto(photo.fullUrl, fileName)) {
                is Result.Success -> {
                    _actionEvent.emit(stringProvider.get(R.string.download_started))
                    reportDownload(photo.downloadLocationUrl ?: return@launch)
                }
                is Result.Error -> _actionEvent.emit(result.message)
            }
        }
    }

    fun setWallpaper(photo: Photo) {
        viewModelScope.launch {
            _isActionLoading.value = true
            when (val result = photoActionsRepository.setWallpaper(photo.fullUrl)) {
                is Result.Success -> {
                    _actionEvent.emit(stringProvider.get(R.string.wallpaper_set_success))
                    reportDownload(photo.downloadLocationUrl ?: return@launch)
                }
                is Result.Error -> _actionEvent.emit(result.message)
            }
            _isActionLoading.value = false
        }
    }
}

