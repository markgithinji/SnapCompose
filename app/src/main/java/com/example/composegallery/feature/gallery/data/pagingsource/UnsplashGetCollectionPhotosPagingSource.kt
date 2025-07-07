package com.example.composegallery.feature.gallery.data.pagingsource

import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.util.StringProvider

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
