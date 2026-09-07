package com.example.composegallery.feature.profile.data

import com.example.composegallery.core.network.model.toDomainModel
import com.example.composegallery.core.network.paging.BaseUnsplashPagingSource
import com.example.composegallery.core.network.remote.UnsplashApi
import com.example.composegallery.core.common.StringProvider
import com.example.composegallery.core.domain.model.PhotoCollection

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
