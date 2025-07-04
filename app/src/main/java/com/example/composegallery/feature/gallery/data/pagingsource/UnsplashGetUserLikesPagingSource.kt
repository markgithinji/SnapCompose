package com.example.composegallery.feature.gallery.data.pagingsource

import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.Photo

class UnsplashGetUserLikesPagingSource(
    private val api: UnsplashApi,
    private val username: String
) : BaseUnsplashPagingSource<Photo>(
    api = { page, perPage ->
        api.getUserLikedPhotos(username = username, page = page, perPage = perPage)
            .mapNotNull { it.toDomainModel() }
    }
)
