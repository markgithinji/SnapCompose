package com.example.composegallery.feature.gallery.data.pagingsource

import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.PhotoCollection
import com.example.composegallery.feature.gallery.util.StringProvider

class UnsplashGetUserCollectionsPagingSource(
    private val api: UnsplashApi,
    private val username: String,
    stringProvider: StringProvider
) : BaseUnsplashPagingSource<PhotoCollection>(
    stringProvider = stringProvider,
    api = { page, perPage ->
        api.getUserCollections(
            username = username,
            page = page,
            perPage = perPage
        ).map { it.toDomainModel() }
    }
)