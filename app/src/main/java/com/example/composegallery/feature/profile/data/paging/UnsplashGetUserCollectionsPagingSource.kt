package com.example.composegallery.feature.profile.data.paging

import com.example.composegallery.core.data.model.toDomainModel
import com.example.composegallery.core.data.paging.BaseUnsplashPagingSource
import com.example.composegallery.core.data.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider
import com.example.composegallery.feature.profile.domain.model.PhotoCollection

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
