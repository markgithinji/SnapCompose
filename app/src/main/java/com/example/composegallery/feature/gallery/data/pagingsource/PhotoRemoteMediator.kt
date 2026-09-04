package com.example.composegallery.feature.gallery.data.pagingsource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.composegallery.feature.gallery.data.local.AppDatabase
import com.example.composegallery.feature.gallery.data.local.PhotoEntity
import com.example.composegallery.feature.gallery.data.local.PhotoRemoteKeyEntity
import com.example.composegallery.feature.gallery.data.local.toEntity
import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.data.util.safeApiCall
import com.example.composegallery.feature.gallery.util.StringProvider
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val api: UnsplashApi,
    private val database: AppDatabase,
    private val stringProvider: StringProvider,
    private val topicId: String // "editorial" or topic slug
) : RemoteMediator<Int, PhotoEntity>() {

    companion object {
        const val EDITORIAL = "editorial"
    }

    override suspend fun initialize(): InitializeAction {
        val count = database.photoDao().getCount(topicId)
        Timber.tag("PhotoRemoteMediator").d("initialize: topicId=$topicId, count=$count")
        // If we already have data, skip the initial refresh to keep the grid stable.
        // The user can still pull-to-refresh manually.
        return if (count > 0) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoEntity>
    ): MediatorResult {
        Timber.tag("PhotoRemoteMediator").d("load: START topicId=$topicId, loadType=$loadType, anchorPosition=${state.anchorPosition}")
        
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
            Timber.tag("PhotoRemoteMediator").d("load: Fetching topic=$topicId page $page from API...")
            if (topicId == EDITORIAL) {
                api.getPhotos(page = page, perPage = state.config.pageSize)
            } else {
                api.getTopicPhotos(topicIdOrSlug = topicId, page = page, perPage = state.config.pageSize)
            }
        }

        return when (result) {
            is Result.Success -> {
                val photos = result.data.mapNotNull { it.toDomainModel() }
                Timber.tag("PhotoRemoteMediator").d("load: SUCCESS topic=$topicId fetched ${photos.size} photos for page $page")
                val endOfPaginationReached = photos.isEmpty()

                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.photoRemoteKeyDao().clearRemoteKeys(topicId)
                        database.photoDao().clearAll(topicId)
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
