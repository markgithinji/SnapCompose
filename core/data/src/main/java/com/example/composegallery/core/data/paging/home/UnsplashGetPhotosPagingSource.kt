package com.example.composegallery.core.data.paging.home

import com.example.composegallery.core.data.model.toDomainModel
import com.example.composegallery.core.data.paging.BaseUnsplashPagingSource
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.data.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider

class UnsplashGetPhotosPagingSource(
    private val api: UnsplashApi,
    stringProvider: StringProvider
) : BaseUnsplashPagingSource<Photo>(
    stringProvider = stringProvider,
    api = { page, perPage ->
        api.getPhotos(page = page, perPage = perPage)
            .mapNotNull { it.toDomainModel() }
    }
)
