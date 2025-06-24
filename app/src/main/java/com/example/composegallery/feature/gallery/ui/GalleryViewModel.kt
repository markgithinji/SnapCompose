package com.example.composegallery.feature.gallery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    galleryRepository: GalleryRepository
) : ViewModel() {

    val pagedPhotos: Flow<PagingData<Photo>> =
        galleryRepository.getPagedPhotos().cachedIn(viewModelScope)
}
