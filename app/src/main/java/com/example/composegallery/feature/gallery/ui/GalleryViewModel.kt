package com.example.composegallery.feature.gallery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composegallery.feature.gallery.data.repository.DefaultGalleryRepository
import com.example.composegallery.feature.gallery.domain.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val defaultGalleryRepository: DefaultGalleryRepository
) : ViewModel() {

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos

    init {
        loadImages()
    }

    private fun loadImages() {
        viewModelScope.launch {
            try {
                val result = defaultGalleryRepository.getPhotos()
                _photos.value = result
            } catch (e: Exception) {
                // Handle error (log or expose UI state)
                e.printStackTrace()
            }
        }
    }
}
