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
    private val _collectionId = MutableStateFlow<String?>(null)

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

    val userPhotos: Flow<PagingData<Photo>> = _username
        .filterNotNull()
        .flatMapLatest { username ->
            userRepository.getUserPhotos(username)
        }
        .cachedIn(viewModelScope)

    val userCollectionsState: Flow<PagingData<PhotoCollection>> = _username
        .filterNotNull()
        .flatMapLatest { username ->
            userRepository.getUserCollections(username)
        }
        .cachedIn(viewModelScope)

    val userLikedPhotos: Flow<PagingData<Photo>> = _username
        .filterNotNull()
        .flatMapLatest { username ->
            userRepository.getUserLikedPhotos(username)
        }
        .cachedIn(viewModelScope)

    val collectionPhotos: Flow<PagingData<Photo>> = _collectionId
        .filterNotNull()
        .flatMapLatest { collectionId ->
            userRepository.getCollectionPhotos(collectionId)
        }
        .cachedIn(viewModelScope)

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

    fun setCollectionId(collectionId: String) {
        if (_collectionId.value != collectionId) {
            _collectionId.value = collectionId
        }
    }

    // No-op methods kept for compatibility with UI calls, but refactored to be reactive
    fun loadUserPhotos(username: String) {}
    fun loadUserCollections(username: String) {}
    fun loadUserLikedPhotos(username: String) {}
    fun loadCollectionPhotos(collectionId: String) {}
    fun loadUserStatistics(username: String) {}
}
