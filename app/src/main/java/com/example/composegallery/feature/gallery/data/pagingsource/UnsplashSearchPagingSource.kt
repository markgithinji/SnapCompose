package com.example.composegallery.feature.gallery.data.pagingsource

import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.util.StringProvider

class UnsplashSearchPagingSource(
    private val api: UnsplashApi,
    private val query: String,
    stringProvider: StringProvider
) : BaseUnsplashPagingSource<Photo>(
    stringProvider = stringProvider,
    api = { page, perPage ->
        if (query.isBlank()) emptyList()
        else api.searchPhotos(query = query, page = page, perPage = perPage)
            .results.mapNotNull { it.toDomainModel() }
    }
)
