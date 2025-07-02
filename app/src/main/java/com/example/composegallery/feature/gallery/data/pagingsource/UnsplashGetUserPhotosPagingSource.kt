package com.example.composegallery.feature.gallery.data.pagingsource

import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.Photo

class UnsplashGetUserPhotosPagingSource(
    private val api: UnsplashApi,
    private val username: String
) : BasePhotoPagingSource<Photo>(
    apiCall = { page, perPage ->
        api.getUserPhotos(username = username, page = page, perPage = perPage)
            .mapNotNull { it.toDomainModel() }
    }
)
