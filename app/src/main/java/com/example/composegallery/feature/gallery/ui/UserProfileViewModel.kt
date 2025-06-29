package com.example.composegallery.feature.gallery.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Collection
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import com.example.composegallery.feature.gallery.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfileState = MutableStateFlow<UiState<UnsplashUser>>(UiState.Loading)
    val userProfileState: StateFlow<UiState<UnsplashUser>> = _userProfileState.asStateFlow()

    private val _pagedUserPhotos = MutableStateFlow(PagingData.empty<Photo>())
    val pagedUserPhotos: StateFlow<PagingData<Photo>> = _pagedUserPhotos

    private val _userCollectionsState = MutableStateFlow<PagingData<Collection>>(PagingData.empty())
    val userCollectionsState: StateFlow<PagingData<Collection>> = _userCollectionsState

    private val _collectionPhotos = MutableStateFlow(PagingData.empty<Photo>())
    val collectionPhotos: StateFlow<PagingData<Photo>> = _collectionPhotos

    private val _userLikedPhotos = MutableStateFlow<PagingData<Photo>>(PagingData.empty())
    val userLikedPhotos: StateFlow<PagingData<Photo>> = _userLikedPhotos

    fun loadUserProfile(username: String) {
        viewModelScope.launch {
            _userProfileState.value = UiState.Loading
            when (val result = userRepository.getUserProfile(username)) {
                is Result.Success -> _userProfileState.value = UiState.Content(result.data)
                is Result.Error -> _userProfileState.value = UiState.Error(result.message)
            }
        }
    }

    fun loadUserPhotos(username: String) {
        viewModelScope.launch {
            userRepository.getUserPhotos(username)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _pagedUserPhotos.value = pagingData
                }
        }
    }

    fun loadUserCollections(username: String) {
        viewModelScope.launch {
            userRepository.getUserCollections(username)
                .flow
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
}
