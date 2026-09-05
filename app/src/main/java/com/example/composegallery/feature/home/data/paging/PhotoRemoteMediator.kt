package com.example.composegallery.feature.home.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.composegallery.core.data.Result
import com.example.composegallery.core.data.model.toDomainModel
import com.example.composegallery.core.data.safeApiCall
import com.example.composegallery.core.data.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider
import com.example.composegallery.core.data.local.AppDatabase
import com.example.composegallery.feature.home.data.local.PhotoEntity
import com.example.composegallery.feature.home.data.local.PhotoRemoteKeyEntity
import com.example.composegallery.feature.home.data.local.TopicCacheMetadataEntity
import com.example.composegallery.feature.home.data.local.toEntity
import timber.log.Timber
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val api: UnsplashApi,
    private val database: AppDatabase,
    private val stringProvider: StringProvider,
    private val topicId: String
) : RemoteMediator<Int, PhotoEntity>() {

    companion object {
        const val EDITORIAL = "editorial"
        private const val CACHE_TIMEOUT_SECONDS = 3600L
    }

    override suspend fun initialize(): InitializeAction {
        val count = database.photoDao().getCount(topicId)
        val metadata = database.topicCacheMetadataDao().getMetadata(topicId)
        
        val lastUpdated = metadata?.lastUpdated ?: 0L
        val now = System.currentTimeMillis()
        val diffSeconds = TimeUnit.MILLISECONDS.toSeconds(now - lastUpdated)
        val isStale = diffSeconds > CACHE_TIMEOUT_SECONDS

        return if (count > 0 && !isStale) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                val nextPage = remoteKey?.nextPage
                if (nextPage == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                }
                nextPage
            }
        }

        val result = safeApiCall(stringProvider) {
            if (topicId == EDITORIAL) {
                api.getPhotos(page = page, perPage = state.config.pageSize)
            } else {
                api.getTopicPhotos(topicIdOrSlug = topicId, page = page, perPage = state.config.pageSize)
            }
        }

        return when (result) {
            is Result.Success -> {
                val photos = result.data.mapNotNull { it.toDomainModel() }
                val endOfPaginationReached = photos.isEmpty()

                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.photoRemoteKeyDao().clearRemoteKeys(topicId)
                        database.photoDao().clearAll(topicId)
                        database.topicCacheMetadataDao().insertMetadata(
                            TopicCacheMetadataEntity(topicId = topicId, lastUpdated = System.currentTimeMillis())
                        )
                    }

                    val prevPage = if (page == 1) null else page - 1
                    val nextPage = if (endOfPaginationReached) null else page + 1
                    
                    val keys = photos.map {
                        PhotoRemoteKeyEntity(photoId = it.id, topicId = topicId, prevPage = prevPage, nextPage = nextPage)
                    }
                    database.photoRemoteKeyDao().insertAll(keys)

                    val entities = photos.mapIndexed { index, photo ->
                        val order = (page - 1) * state.config.pageSize + index
                        photo.toEntity(topicId = topicId, pagingOrder = order)
                    }

                    val insertResults = database.photoDao().insertPhotos(entities)
                    
                    insertResults.forEachIndexed { index, resultId ->
                        val entity = entities[index]
                        if (resultId == -1L) {
                            if (loadType == LoadType.REFRESH) {
                                database.photoDao().updatePagingOrder(entity.id, topicId, entity.pagingOrder)
                            }
                        }
                    }
                }

                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }

            is Result.Error -> {
                Timber.tag("PhotoRemoteMediator").e("load: ERROR topic=$topicId for page $page: ${result.message}")
                MediatorResult.Error(result.throwable ?: Exception(result.message))
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PhotoEntity>): PhotoRemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { photo ->
                database.photoRemoteKeyDao().getRemoteKeyByPhotoId(photo.id, topicId)
            }
    }
}
