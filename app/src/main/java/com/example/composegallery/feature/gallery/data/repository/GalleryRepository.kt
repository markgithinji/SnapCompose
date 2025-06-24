package com.example.composegallery.feature.gallery.data.repository

import android.util.Log
import com.example.composegallery.BuildConfig
import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.Photo
import timber.log.Timber

class GalleryRepository(
    private val api: UnsplashApi
) {
    suspend fun getPhotos(page: Int = 1, perPage: Int = 20): List<Photo> {
        val accessKey = BuildConfig.UNSPLASH_API_KEY

        val result = api.getPhotos(page, perPage, accessKey)
            .mapNotNull { it.toDomainModel()}
        return result
    }
}
