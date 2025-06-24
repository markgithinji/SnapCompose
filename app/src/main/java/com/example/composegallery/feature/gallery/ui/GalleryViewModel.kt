package com.example.composegallery.feature.gallery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository
) : ViewModel() {

    private val _photos = MutableStateFlow<Result<List<Photo>>>(Result.Loading)
    val photos: StateFlow<Result<List<Photo>>> = _photos

    init {
        loadImages()
    }

    private fun loadImages() {
        viewModelScope.launch {
            galleryRepository.getPhotos().collect { result ->
                _photos.value = result
            }
        }
    }
}
