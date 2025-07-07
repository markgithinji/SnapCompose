package com.example.composegallery.feature.gallery.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.filter
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.pagingsource.PagingDefaults
import com.example.composegallery.feature.gallery.data.pagingsource.UnsplashGetPhotosPagingSource
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.data.util.safeApiCall
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import com.example.composegallery.feature.gallery.util.StringProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultGalleryRepository @Inject constructor(
    private val api: UnsplashApi,
    private val stringProvider: StringProvider
) : GalleryRepository {

    override fun getPagedPhotos(): Flow<PagingData<Photo>> {
        return createPager { UnsplashGetPhotosPagingSource(api, stringProvider) }
    }

    private fun createPager(
        pagingSourceFactory: () -> PagingSource<Int, Photo>
    ): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingDefaults.PAGE_SIZE,
                initialLoadSize = PagingDefaults.INITIAL_LOAD_SIZE,
                prefetchDistance = PagingDefaults.PREFETCH_DISTANCE
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
            .distinctUntilChanged()
            .map { pagingData ->
                val seen = mutableSetOf<String>()
                pagingData.filter { seen.add(it.id) } // Filter duplicates
            }
    }

    override suspend fun getPhoto(photoId: String): Result<Photo> {
        return safeApiCall(stringProvider) {
            val response = api.getPhoto(photoId = photoId)
            response.toDomainModel()
                ?: throw IllegalStateException(stringProvider.get(R.string.error_invalid_data_received))
        }
    }
}

