package com.example.composegallery.feature.gallery.data.repository

import com.example.composegallery.BuildConfig
import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import javax.inject.Inject

class DefaultGalleryRepository @Inject constructor(
    private val api: UnsplashApi
) : GalleryRepository {
    override suspend fun getPhotos(page: Int, perPage: Int): List<Photo> {
        val accessKey = BuildConfig.UNSPLASH_API_KEY

        val result = api.getPhotos(page, perPage, accessKey)
            .mapNotNull { it.toDomainModel()}
        return result
    }
}
