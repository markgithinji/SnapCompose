package com.example.composegallery.feature.gallery.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.filter
import com.example.composegallery.feature.gallery.data.pagingsource.UnsplashGetPhotosPagingSource
import com.example.composegallery.feature.gallery.data.pagingsource.UnsplashSearchPagingSource
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultGalleryRepository @Inject constructor(
    private val api: UnsplashApi
) : GalleryRepository {

    override fun getPagedPhotos(): Flow<PagingData<Photo>> {
        return createPager { UnsplashGetPhotosPagingSource(api) }
    }

    override fun searchPagedPhotos(query: String): Flow<PagingData<Photo>> {
        return createPager { UnsplashSearchPagingSource(api, query) }
    }

    private fun createPager(
        pagingSourceFactory: () -> PagingSource<Int, Photo>
    ): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = pagingSourceFactory
        ).flow
            .distinctUntilChanged()
            .map { pagingData ->
                val seen = mutableSetOf<String>()
                pagingData.filter { seen.add(it.id) } // Filter out duplicates TODO: Find out source of duplicates
            }
    }

}
