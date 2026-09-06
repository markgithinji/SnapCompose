package com.example.composegallery.feature.profile.data

import com.example.composegallery.core.network.model.toDomainModel
import com.example.composegallery.core.network.paging.BaseUnsplashPagingSource
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.network.remote.UnsplashApi
import com.example.composegallery.core.common.StringProvider

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
