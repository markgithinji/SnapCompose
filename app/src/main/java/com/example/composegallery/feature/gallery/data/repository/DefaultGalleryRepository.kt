package com.example.composegallery.feature.gallery.data.repository

import com.example.composegallery.BuildConfig
import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.data.util.safeApiCall
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DefaultGalleryRepository @Inject constructor(
    private val api: UnsplashApi
) : GalleryRepository {

    override suspend fun getPhotos(page: Int, perPage: Int): Flow<Result<List<Photo>>> = flow {
        emit(Result.Loading)

        val result = safeApiCall {
            api.getPhotos(page, perPage, BuildConfig.UNSPLASH_API_KEY)
                .mapNotNull { it.toDomainModel() }
        }
        emit(result)
    }
}
