package com.example.composegallery.feature.gallery.domain.repository

import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    suspend fun getPhotos(page: Int = 1, perPage: Int = 20): Flow<Result<List<Photo>>>
}