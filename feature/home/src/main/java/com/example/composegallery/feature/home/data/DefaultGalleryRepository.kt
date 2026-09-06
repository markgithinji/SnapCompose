package com.example.composegallery.feature.home.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.composegallery.core.ui.R
import com.example.composegallery.core.model.Result
import com.example.composegallery.core.network.safeApiCall
import com.example.composegallery.core.network.model.toDomainModel
import com.example.composegallery.core.network.paging.PagingDefaults
import com.example.composegallery.core.network.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider
import com.example.composegallery.core.database.local.AppDatabase
import com.example.composegallery.core.database.local.toDomainModel
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.model.Topic
import com.example.composegallery.core.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
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
        } catch (e: Exception) {}
    }

    override suspend fun getPhoto(photoId: String): Result<Photo> {
        database.photoDao().getPhotoById(photoId)?.let {
            Timber.tag("GalleryRepository").d("getPhoto: Returning cached version from DB for %s", photoId)
            return Result.Success(it.toDomainModel())
        }

        return safeApiCall(stringProvider) {
            val response = api.getPhoto(photoId = photoId)
            val photo = response.toDomainModel()
                ?: throw IllegalStateException(stringProvider.get(R.string.error_invalid_data_received))
            
            photo
        }
    }
}
