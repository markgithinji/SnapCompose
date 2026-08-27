package com.example.composegallery.feature.gallery.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.FavoriteRepository
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import com.example.composegallery.feature.gallery.domain.usecase.DownloadPhotoUseCase
import com.example.composegallery.feature.gallery.domain.usecase.SetWallpaperUseCase
import com.example.composegallery.feature.gallery.domain.usecase.ToggleFavoriteUseCase
import com.example.composegallery.feature.gallery.ui.util.UiState
import com.example.composegallery.feature.gallery.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    private val favoriteRepository: FavoriteRepository,
    private val downloadPhotoUseCase: DownloadPhotoUseCase,
    private val setWallpaperUseCase: SetWallpaperUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val isFavorite: StateFlow<Boolean> = _uiState
        .flatMapLatest { state ->
            if (state is UiState.Content) {
                favoriteRepository.isFavorite(state.data.id)
            } else {
                flowOf(false)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val favoritePhotos: StateFlow<List<Photo>> = favoriteRepository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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
            when (val result = downloadPhotoUseCase(photo)) {
                is Result.Success -> {
                    _actionEvent.emit(stringProvider.get(R.string.download_started))
                }
                is Result.Error -> _actionEvent.emit(result.message)
            }
        }
    }

    fun setWallpaper(photo: Photo) {
        viewModelScope.launch {
            _isActionLoading.value = true
            when (val result = setWallpaperUseCase(photo)) {
                is Result.Success -> {
                    _actionEvent.emit(stringProvider.get(R.string.wallpaper_set_success))
                }
                is Result.Error -> _actionEvent.emit(result.message)
            }
            _isActionLoading.value = false
        }
    }

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            toggleFavoriteUseCase(photo)
        }
    }
}

