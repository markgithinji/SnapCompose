package com.example.composegallery.feature.gallery.domain.repository

import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    fun getPagedPhotos(): Flow<PagingData<Photo>>
}