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
        return getCachedPagedPhotos(PhotoRemoteMediator.EDITORIAL)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getTopicPagedPhotos(topicIdOrSlug: String): Flow<PagingData<Photo>> {
        return getCachedPagedPhotos(topicIdOrSlug)
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun getCachedPagedPhotos(topicId: String): Flow<PagingData<Photo>> {
        // We include topicId in the Pager's configuration or use a unique PagingSourceFactory
        // to ensure that switching topics actually creates a new Paging pipeline.
        return Pager(
            config = PagingConfig(
                pageSize = PagingDefaults.PAGE_SIZE,
                initialLoadSize = PagingDefaults.INITIAL_LOAD_SIZE,
                prefetchDistance = PagingDefaults.PREFETCH_DISTANCE,
                enablePlaceholders = true
            ),
            remoteMediator = PhotoRemoteMediator(api, database, stringProvider, topicId),
            pagingSourceFactory = { 
                database.photoDao().getPagedPhotos(topicId) 
            }
        ).flow
            .map { pagingData ->
                pagingData.map { it.toDomainModel() }
            }
    }

    override suspend fun getTopics(): Result<List<Topic>> {
        return safeApiCall(stringProvider) {
            val response = api.getTopics()
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
        // 1. Check local database cache first
        database.photoDao().getPhotoById(photoId)?.let {
            Timber.tag("GalleryRepository").d("getPhoto: Returning cached version from DB for %s", photoId)
            return Result.Success(it.toDomainModel())
        }

        // 2. Fetch from API if not in DB
        return safeApiCall(stringProvider) {
            val response = api.getPhoto(photoId = photoId)
            val photo = response.toDomainModel()
                ?: throw IllegalStateException(stringProvider.get(R.string.error_invalid_data_received))
            
            photo
        }
    }
}
