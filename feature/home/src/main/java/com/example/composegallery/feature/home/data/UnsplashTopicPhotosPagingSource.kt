package com.example.composegallery.feature.home.data

import com.example.composegallery.core.network.model.toDomainModel
import com.example.composegallery.core.network.paging.BaseUnsplashPagingSource
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.network.remote.UnsplashApi
import com.example.composegallery.core.common.StringProvider

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
