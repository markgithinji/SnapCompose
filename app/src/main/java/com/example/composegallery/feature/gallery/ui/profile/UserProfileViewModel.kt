package com.example.composegallery.feature.gallery.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.PhotoCollection
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import com.example.composegallery.feature.gallery.domain.model.UserStatistics
import com.example.composegallery.feature.gallery.domain.repository.UserRepository
import com.example.composegallery.feature.gallery.ui.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _username = MutableStateFlow<String?>(null)

    val userProfileState: StateFlow<UiState<UnsplashUser>> = _username
        .filterNotNull()
        .flatMapLatest { username ->
            flow {
                emit(UiState.Loading)
                when (val result = userRepository.getUserProfile(username)) {
                    is Result.Success -> emit(UiState.Content(result.data))
                    is Result.Error -> emit(UiState.Error(result.message))
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    private val _userPhotos = MutableStateFlow(PagingData.empty<Photo>())
    val userPhotos: StateFlow<PagingData<Photo>> = _userPhotos

    private val _userCollectionsState =
        MutableStateFlow<PagingData<PhotoCollection>>(PagingData.empty())
    val userCollectionsState: StateFlow<PagingData<PhotoCollection>> = _userCollectionsState

    private val _collectionPhotos = MutableStateFlow(PagingData.empty<Photo>())
    val collectionPhotos: StateFlow<PagingData<Photo>> = _collectionPhotos

    private val _userLikedPhotos = MutableStateFlow<PagingData<Photo>>(PagingData.empty())
    val userLikedPhotos: StateFlow<PagingData<Photo>> = _userLikedPhotos

    val userStatisticsState: StateFlow<UiState<UserStatistics>> = _username
        .filterNotNull()
        .flatMapLatest { username ->
            flow {
                emit(UiState.Loading)
                when (val result = userRepository.getUserStatistics(username)) {
                    is Result.Success -> emit(UiState.Content(result.data))
                    is Result.Error -> emit(UiState.Error(result.message))
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    fun setUsername(username: String) {
        if (_username.value != username) {
            _username.value = username
        }
    }

    fun loadUserPhotos(username: String) {
        viewModelScope.launch {
            userRepository.getUserPhotos(username)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _userPhotos.value = pagingData
                }
        }
    }

    fun loadUserCollections(username: String) {
        viewModelScope.launch {
            userRepository.getUserCollections(username)
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _userCollectionsState.value = pagingData
                }
        }
    }

    fun loadUserLikedPhotos(username: String) {
        viewModelScope.launch {
            userRepository.getUserLikedPhotos(username)
                .cachedIn(viewModelScope)
                .collectLatest {
                    _userLikedPhotos.value = it
                }
        }
    }

    fun loadCollectionPhotos(collectionId: String) {
        viewModelScope.launch {
            userRepository.getCollectionPhotos(collectionId)
                .cachedIn(viewModelScope)
                .collectLatest {
                    _collectionPhotos.value = it
                }
        }
    }

    fun loadUserStatistics(username: String) {
        // Handled reactively by userStatisticsState
    }
}
