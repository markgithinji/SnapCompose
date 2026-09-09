package com.example.composegallery.feature.home.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.composegallery.core.common.R
import com.example.composegallery.core.common.Result
import com.example.composegallery.core.common.safeApiCall
import com.example.composegallery.core.network.model.toDomainModel
import com.example.composegallery.core.network.paging.PagingDefaults
import com.example.composegallery.core.network.remote.UnsplashApi
import com.example.composegallery.core.common.StringProvider
import com.example.composegallery.core.database.local.AppDatabase
import com.example.composegallery.core.database.local.home.entity.PhotoRemoteKeyEntity
import com.example.composegallery.core.database.local.home.entity.TopicCacheMetadataEntity
import com.example.composegallery.core.database.local.home.entity.toDomainModel
import com.example.composegallery.core.database.local.home.entity.toEntity
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.domain.model.Topic
import com.example.composegallery.core.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
            remoteMediator = PhotoRemoteMediator(database, this, topicId),
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
        }
    }

    override suspend fun reportDownload(downloadUrl: String) {
        try {
            api.triggerDownload(downloadUrl)
        } catch (e: Exception) {}
    }

    override suspend fun getPhoto(photoId: String): Result<Photo> {
        database.photoDao().getPhotoById(photoId)?.let {
            return Result.Success(it.toDomainModel())
        }

        return safeApiCall(stringProvider) {
            val response = api.getPhoto(photoId = photoId)
            val photo = response.toDomainModel()
                ?: throw IllegalStateException(stringProvider.get(R.string.error_invalid_data_received))
            
            photo
        }
    }

    override suspend fun syncPhotos(
        topicId: String,
        page: Int,
        pageSize: Int,
        isRefresh: Boolean
    ): Result<Boolean> {
        return safeApiCall(stringProvider) {
            val response = if (topicId == PhotoRemoteMediator.EDITORIAL) {
                api.getPhotos(page = page, perPage = pageSize)
            } else {
                api.getTopicPhotos(topicIdOrSlug = topicId, page = page, perPage = pageSize)
            }

            val photos = response.mapNotNull { it.toDomainModel() }
            val endOfPaginationReached = photos.isEmpty()

            database.withTransaction {
                if (isRefresh) {
                    database.photoRemoteKeyDao().clearRemoteKeys(topicId)
                    database.photoDao().clearAll(topicId)
                    database.topicCacheMetadataDao().insertMetadata(
                        TopicCacheMetadataEntity(topicId = topicId, lastUpdated = System.currentTimeMillis())
                    )
                }

                val prevPage = if (page == 1) null else page - 1
                val nextPage = if (endOfPaginationReached) null else page + 1

                val keys = photos.map {
                    PhotoRemoteKeyEntity(
                        photoId = it.id,
                        topicId = topicId,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                database.photoRemoteKeyDao().insertAll(keys)

                val entities = photos.mapIndexed { index, photo ->
                    val order = (page - 1) * pageSize + index
                    photo.toEntity(topicId = topicId, pagingOrder = order)
                }

                val insertResults = database.photoDao().insertPhotos(entities)

                if (isRefresh) {
                    insertResults.forEachIndexed { index, resultId ->
                        if (resultId == -1L) {
                            val entity = entities[index]
                            database.photoDao().updatePagingOrder(entity.id, topicId, entity.pagingOrder)
                        }
                    }
                }
            }
            endOfPaginationReached
        }
    }
}
