package com.example.composegallery.feature.gallery.data.pagingsource

import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.Photo

class UnsplashGetPhotosPagingSource(
    private val api: UnsplashApi
) : BaseUnsplashPagingSource<Photo>(
    api = { page, perPage ->
        api.getPhotos(page = page, perPage = perPage)
            .mapNotNull { it.toDomainModel() }
    }
)