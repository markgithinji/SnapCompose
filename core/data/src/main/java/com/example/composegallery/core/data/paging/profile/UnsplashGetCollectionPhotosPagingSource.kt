package com.example.composegallery.core.data.paging.profile

import com.example.composegallery.core.data.model.toDomainModel
import com.example.composegallery.core.data.paging.BaseUnsplashPagingSource
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.data.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider

class UnsplashGetCollectionPhotosPagingSource(
    private val api: UnsplashApi,
    private val collectionId: String,
    stringProvider: StringProvider
) : BaseUnsplashPagingSource<Photo>(
    stringProvider = stringProvider,
    api = { page, perPage ->
        api.getCollectionPhotos(collectionId = collectionId, page = page, perPage = perPage)
            .mapNotNull { it.toDomainModel() }
    }
)
