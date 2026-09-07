package com.example.composegallery.feature.photodetail.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composegallery.core.common.Result
import com.example.composegallery.core.common.message.MessageDuration
import com.example.composegallery.core.common.message.MessageManager
import com.example.composegallery.core.domain.model.DownloadStatus
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.common.UiState
import com.example.composegallery.core.common.StringProvider
import com.example.composegallery.core.domain.repository.FavoriteRepository
import com.example.composegallery.core.domain.repository.GalleryRepository
import com.example.composegallery.feature.photodetail.domain.usecase.DownloadPhotoUseCase
import com.example.composegallery.feature.photodetail.domain.usecase.SetWallpaperUseCase
import com.example.composegallery.feature.photodetail.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import com.example.composegallery.core.ui.R as UiR

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    private val favoriteRepository: FavoriteRepository,
    private val downloadPhotoUseCase: DownloadPhotoUseCase,
    private val setWallpaperUseCase: SetWallpaperUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val stringProvider: StringProvider,
    private val messageManager: MessageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Photo>>(UiState.Loading)
    val uiState: StateFlow<UiState<Photo>> = _uiState

    private val _isWallpaperLoading = MutableStateFlow(false)
    val isWallpaperLoading: StateFlow<Boolean> = _isWallpaperLoading

    private val _downloadStatus = MutableStateFlow<DownloadStatus>(DownloadStatus.Idle)
    val downloadStatus: StateFlow<DownloadStatus> = _downloadStatus.asStateFlow()

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

    fun loadPhoto(photoId: String) {
        val currentState = _uiState.value
        if (currentState is UiState.Content && currentState.data.id == photoId) {
            return
        }

        viewModelScope.launch {
            val result = galleryRepository.getPhoto(photoId)
            if (result is Result.Success) {
                _uiState.value = UiState.Content(result.data)
            } else if (result is Result.Error) {
                _uiState.value = UiState.Error(result.message)
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
            downloadPhotoUseCase(photo).collect { status ->
                _downloadStatus.value = status
                if (status is DownloadStatus.Success) {
                    messageManager.showMessage(
                        message = stringProvider.get(UiR.string.download_success_path).format(status.path),
                        actionLabel = stringProvider.get(UiR.string.dismiss),
                        duration = MessageDuration.Indefinite
                    )
                } else if (status is DownloadStatus.Error) {
                    messageManager.showMessage(status.message)
                }
            }
        }
    }

    fun resetDownloadStatus() {
        _downloadStatus.value = DownloadStatus.Idle
    }

    fun setWallpaper(photo: Photo) {
        viewModelScope.launch {
            _isWallpaperLoading.value = true
            when (val result = setWallpaperUseCase(photo)) {
                is Result.Success<*> -> {
                    messageManager.showMessage(stringProvider.get(UiR.string.wallpaper_set_success))
                }
                is Result.Error -> messageManager.showMessage(result.message)
            }
            _isWallpaperLoading.value = false
        }
    }

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            toggleFavoriteUseCase(photo)
        }
    }
}
