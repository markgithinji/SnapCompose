package com.example.composegallery.feature.gallery.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.Topic
import com.example.composegallery.feature.gallery.domain.repository.FavoriteRepository
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import com.example.composegallery.feature.gallery.domain.usecase.DownloadPhotoUseCase
import com.example.composegallery.feature.gallery.domain.usecase.SetWallpaperUseCase
import com.example.composegallery.feature.gallery.domain.usecase.ToggleFavoriteUseCase
import com.example.composegallery.feature.gallery.ui.util.UiState
import com.example.composegallery.feature.gallery.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

    val gridState = LazyStaggeredGridState()

    private val _uiState = MutableStateFlow<UiState<Photo>>(UiState.Loading)
    val uiState: StateFlow<UiState<Photo>> = _uiState

    private val _actionEvent = MutableSharedFlow<String>()
    val actionEvent: SharedFlow<String> = _actionEvent.asSharedFlow()

    private val _isActionLoading = MutableStateFlow(false)
    val isActionLoading: StateFlow<Boolean> = _isActionLoading

    private val _topicsState = MutableStateFlow<UiState<List<Topic>>>(UiState.Loading)
    val topicsState: StateFlow<UiState<List<Topic>>> = _topicsState.asStateFlow()

    private val _selectedTopicId = MutableStateFlow<String?>(null) // null = Editorial
    val selectedTopicId: StateFlow<String?> = _selectedTopicId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedPhotos: Flow<PagingData<Photo>> = _selectedTopicId
        .flatMapLatest { topicId ->
            Timber.tag("GalleryViewModel").d("Switching to topic: %s", topicId ?: "Editorial")
            val flow = if (topicId == null) {
                galleryRepository.getPagedPhotos()
            } else {
                galleryRepository.getTopicPagedPhotos(topicId)
            }
            flow.cachedIn(viewModelScope)
        }

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

    val favoritesState: StateFlow<UiState<List<Photo>>> = favoriteRepository.getFavorites()
        .map { photos -> 
            UiState.Content(photos) as UiState<List<Photo>> 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    init {
        fetchTopics()
    }

    fun fetchTopics() {
        viewModelScope.launch {
            Timber.tag("GalleryViewModel").d("fetchTopics: Starting load")
            _topicsState.value = UiState.Loading
            try {
                val result = galleryRepository.getTopics()
                Timber.tag("GalleryViewModel").d("fetchTopics: Result received: %s", result)
                when (result) {
                    is Result.Success -> _topicsState.value = UiState.Content(result.data)
                    is Result.Error -> _topicsState.value = UiState.Error(result.message)
                }
            } catch (e: Exception) {
                Timber.tag("GalleryViewModel").e(e, "fetchTopics: Unexpected exception")
                _topicsState.value = UiState.Error(stringProvider.get(R.string.error_unexpected))
            }
        }
    }

    fun selectTopic(topicId: String?) {
        _selectedTopicId.value = topicId
    }

    fun loadPhoto(photoId: String) {
        // 1. If we already have this exact photo in state, skip to avoid flicker
        val currentState = _uiState.value
        if (currentState is UiState.Content && currentState.data.id == photoId) {
            return
        }

        viewModelScope.launch {
            // 2. Fetch from repository (which has an in-memory cache)
            val result = galleryRepository.getPhoto(photoId)
            
            if (result is Result.Success) {
                _uiState.value = UiState.Content(result.data)
            } else if (result is Result.Error) {
                // Only show loading if we don't have a cached version and this is a fresh load
                if (_uiState.value !is UiState.Content) {
                    _uiState.value = UiState.Loading
                }
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

