package com.example.composegallery.feature.gallery.data.pagingsource

import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.Photo

class UnsplashSearchPagingSource(
    private val api: UnsplashApi,
    private val query: String
) : BaseUnsplashPagingSource<Photo>(
    api = { page, perPage ->
        if (query.isBlank()) emptyList()
        else api.searchPhotos(query = query, page = page, perPage = perPage)
            .results.mapNotNull { it.toDomainModel() }
    }
)
