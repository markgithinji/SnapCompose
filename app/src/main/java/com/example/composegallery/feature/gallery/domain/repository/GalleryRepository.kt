package com.example.composegallery.feature.gallery.domain.repository

import com.example.composegallery.feature.gallery.domain.model.Photo

interface GalleryRepository {
    suspend fun getPhotos(page: Int = 1, perPage: Int = 20): List<Photo>
}
