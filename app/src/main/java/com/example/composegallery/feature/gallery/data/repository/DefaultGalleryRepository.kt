package com.example.composegallery.feature.gallery.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.data.local.AppDatabase
import com.example.composegallery.feature.gallery.data.local.toDomainModel
import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.pagingsource.PagingDefaults
import com.example.composegallery.feature.gallery.data.pagingsource.PhotoRemoteMediator
import com.example.composegallery.feature.gallery.data.pagingsource.UnsplashTopicPhotosPagingSource
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.data.util.safeApiCall
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.Topic
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import com.example.composegallery.feature.gallery.util.StringProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class DefaultGalleryRepository @Inject constructor(
    private val api: UnsplashApi,
    private val database: AppDatabase,
    private val stringProvider: StringProvider
) : GalleryRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedPhotos(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingDefaults.PAGE_SIZE,
                initialLoadSize = PagingDefaults.INITIAL_LOAD_SIZE,
                prefetchDistance = PagingDefaults.PREFETCH_DISTANCE
            ),
            remoteMediator = PhotoRemoteMediator(api, database, stringProvider),
            pagingSourceFactory = { database.photoDao().getPagedPhotos() }
        ).flow
            .distinctUntilChanged()
            .map { pagingData ->
                pagingData.map { it.toDomainModel() }
            }
    }

    override fun getTopicPagedPhotos(topicIdOrSlug: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingDefaults.PAGE_SIZE,
                initialLoadSize = PagingDefaults.INITIAL_LOAD_SIZE,
                prefetchDistance = PagingDefaults.PREFETCH_DISTANCE
            ),
            pagingSourceFactory = { UnsplashTopicPhotosPagingSource(api, topicIdOrSlug, stringProvider) }
        ).flow
    }

    override suspend fun getTopics(): Result<List<Topic>> {
        Timber.tag("GalleryRepository").d("getTopics: Fetching from API")
        return safeApiCall(stringProvider) {
            val response = api.getTopics()
            Timber.tag("GalleryRepository").d("getTopics: Received %d topics", response.size)
            response.map { it.toDomainModel() }
        }.also {
            if (it is Result.Error) {
                Timber.tag("GalleryRepository").e("getTopics: Error: %s", it.message)
            }
        }
    }

    override suspend fun reportDownload(downloadUrl: String) {
        try {
            api.triggerDownload(downloadUrl)
        } catch (e: Exception) {
            // Silently fail as per Unsplash recommendation for this non-critical call
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
