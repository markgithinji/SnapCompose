package com.example.composegallery.feature.profile.data

import com.example.composegallery.core.data.model.toDomainModel
import com.example.composegallery.core.data.paging.BaseUnsplashPagingSource
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.data.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider

class UnsplashGetUserPhotosPagingSource(
    private val api: UnsplashApi,
    private val username: String,
    stringProvider: StringProvider
) : BaseUnsplashPagingSource<Photo>(
    stringProvider = stringProvider,
    api = { page, perPage ->
        api.getUserPhotos(username = username, page = page, perPage = perPage)
            .mapNotNull { it.toDomainModel() }
    }
)
