package com.example.composegallery.feature.gallery.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.data.pagingsource.UnsplashPagingSource
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultGalleryRepository @Inject constructor(
    private val api: UnsplashApi
) : GalleryRepository {

    override fun getPagedPhotos(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { UnsplashPagingSource(api) }
        ).flow
    }
}
