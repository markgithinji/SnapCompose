package com.example.composegallery.feature.gallery.data.pagingsource

import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.PhotoCollection

class UnsplashGetUserCollectionsPagingSource(
    private val api: UnsplashApi,
    private val username: String
) : BasePhotoPagingSource<PhotoCollection>(
    apiCall = { page, perPage ->
        api.getUserCollections(
            username = username,
            page = page,
            perPage = perPage
        ).map { it.toDomainModel() }
    }
)