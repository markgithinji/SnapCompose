package com.example.composegallery.feature.home.data.paging

import com.example.composegallery.core.data.model.toDomainModel
import com.example.composegallery.core.data.paging.BaseUnsplashPagingSource
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.data.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider

class UnsplashTopicPhotosPagingSource(
    private val api: UnsplashApi,
    private val topicIdOrSlug: String,
    stringProvider: StringProvider
) : BaseUnsplashPagingSource<Photo>(
    stringProvider = stringProvider,
    api = { page, perPage ->
        api.getTopicPhotos(topicIdOrSlug = topicIdOrSlug, page = page, perPage = perPage)
            .mapNotNull { it.toDomainModel() }
    }
)
