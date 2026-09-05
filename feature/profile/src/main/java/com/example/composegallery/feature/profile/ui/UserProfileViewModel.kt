package com.example.composegallery.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import com.example.composegallery.core.model.Result
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.model.UnsplashUser
import com.example.composegallery.core.util.UiState
import com.example.composegallery.core.model.PhotoCollection
import com.example.composegallery.core.model.UserStatistics
import com.example.composegallery.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // Preserve grid state across navigation and loading state swaps
    val gridState = LazyStaggeredGridState()

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
            started = SharingStarted.Lazily,
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
            started = SharingStarted.Lazily,
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
}
