package com.example.composegallery.feature.search.data

import com.example.composegallery.core.network.model.toDomainModel
import com.example.composegallery.core.network.paging.BaseUnsplashPagingSource
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.network.remote.UnsplashApi
import com.example.composegallery.core.common.StringProvider
import com.example.composegallery.core.domain.model.SearchFilters

class UnsplashSearchPagingSource(
    private val api: UnsplashApi,
    private val filters: SearchFilters,
    stringProvider: StringProvider
) : BaseUnsplashPagingSource<Photo>(
    stringProvider = stringProvider,
    api = { page, perPage ->
        if (filters.query.isBlank()) emptyList()
        else api.searchPhotos(
            query = filters.query,
            page = page,
            perPage = perPage,
            orientation = filters.orientation?.value,
            color = filters.color?.value,
            orderBy = filters.orderBy.value
        ).results.mapNotNull { it.toDomainModel() }
    }
)
