package com.example.composegallery.feature.home.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.composegallery.core.common.Result
import com.example.composegallery.core.database.local.AppDatabase
import com.example.composegallery.core.database.local.home.entity.PhotoEntity
import com.example.composegallery.core.database.local.home.entity.PhotoRemoteKeyEntity
import com.example.composegallery.core.domain.repository.GalleryRepository
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val database: AppDatabase,
    private val repository: GalleryRepository,
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
                val nextPage = remoteKey?.nextPage ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKey != null
                )
                nextPage
            }
        }

        val result = repository.syncPhotos(
            topicId = topicId,
            page = page,
            pageSize = state.config.pageSize,
            isRefresh = loadType == LoadType.REFRESH
        )

        return when (result) {
            is Result.Success -> {
                MediatorResult.Success(endOfPaginationReached = result.data)
            }

            is Result.Error -> {
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
