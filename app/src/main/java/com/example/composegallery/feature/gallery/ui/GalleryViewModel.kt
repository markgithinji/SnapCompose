package com.example.composegallery.feature.gallery.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composegallery.feature.gallery.data.remote.RetrofitInstance
import com.example.composegallery.feature.gallery.data.repository.GalleryRepository
import com.example.composegallery.feature.gallery.domain.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class GalleryViewModel : ViewModel() {

    private val repository = GalleryRepository(RetrofitInstance.api)

    private val _images = MutableStateFlow<List<Photo>>(emptyList())
    val images: StateFlow<List<Photo>> = _images

    init {
        loadImages()
    }

    private fun loadImages() {
        viewModelScope.launch {
            try {
                val result = repository.getPhotos()
                _images.value = result
                Timber.tag("GalleryViewModel").d("Loaded images: $result")
            } catch (e: Exception) {
                // Handle error (log or expose UI state)
                e.printStackTrace()
                Timber.tag("GalleryViewModel").e(e, "Error loading images")
            }
        }
    }
}
