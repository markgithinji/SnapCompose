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
    private val stringProvider: StringProvider
) : RemoteMediator<Int, PhotoEntity>() {

    override suspend fun initialize(): InitializeAction {
        val count = database.photoDao().getCount()
        Timber.tag("PhotoRemoteMediator").d("initialize: count=$count")
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
        Timber.tag("PhotoRemoteMediator").d("load: START loadType=$loadType, anchorPosition=${state.anchorPosition}")
        
        val page = when (loadType) {
            LoadType.REFRESH -> {
                // To keep the list contiguous and stable, we ALWAYS start at page 1 on REFRESH.
                // Resuming from mid-list (e.g. page 5) while clearing everything else
                // causes massive jumps and breaks the PREPEND logic.
                1
            }
            LoadType.PREPEND -> {
                Timber.tag("PhotoRemoteMediator").d("load: PREPEND - Returning Success(endOfPaginationReached=true)")
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                val nextPage = remoteKey?.nextPage
                Timber.tag("PhotoRemoteMediator").d("load: APPEND nextPage=$nextPage (from last remoteKey=${remoteKey?.photoId})")
                if (nextPage == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                }
                nextPage
            }
        }

        val result = safeApiCall(stringProvider) {
            Timber.tag("PhotoRemoteMediator").d("load: Fetching page $page from API...")
            api.getPhotos(page = page, perPage = state.config.pageSize)
        }

        return when (result) {
            is Result.Success -> {
                val photos = result.data.mapNotNull { it.toDomainModel() }
                Timber.tag("PhotoRemoteMediator").d("load: SUCCESS fetched ${photos.size} photos for page $page")
                val endOfPaginationReached = photos.isEmpty()

                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        Timber.tag("PhotoRemoteMediator").d("load: REFRESH - Clearing all data in DB")
                        database.photoRemoteKeyDao().clearRemoteKeys()
                        database.photoDao().clearAll()
                    }

                    val prevPage = if (page == 1) null else page - 1
                    val nextPage = if (endOfPaginationReached) null else page + 1
                    
                    Timber.tag("PhotoRemoteMediator").d("load: DB Transaction for page $page - prevPage=$prevPage, nextPage=$nextPage")

                    val keys = photos.map {
                        PhotoRemoteKeyEntity(photoId = it.id, prevPage = prevPage, nextPage = nextPage)
                    }
                    database.photoRemoteKeyDao().insertAll(keys)

                    val entities = photos.mapIndexed { index, photo ->
                        val order = (page - 1) * state.config.pageSize + index
                        photo.toEntity(pagingOrder = order)
                    }

                    val insertResults = database.photoDao().insertPhotos(entities)
                    
                    insertResults.forEachIndexed { index, resultId ->
                        val entity = entities[index]
                        if (resultId == -1L) {
                            if (loadType == LoadType.REFRESH) {
                                Timber.tag("PhotoRemoteMediator").v("load: Updating existing photo ${entity.id} order to ${entity.pagingOrder}")
                                database.photoDao().updatePagingOrder(entity.id, entity.pagingOrder)
                            } else {
                                Timber.tag("PhotoRemoteMediator").v("load: Photo ${entity.id} already exists, skipping order update during APPEND to maintain stability")
                            }
                        } else {
                            Timber.tag("PhotoRemoteMediator").v("load: Inserted new photo ${entity.id} at order ${entity.pagingOrder}")
                        }
                    }
                }

                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }

            is Result.Error -> {
                Timber.tag("PhotoRemoteMediator").e("load: ERROR for page $page: ${result.message}")
                MediatorResult.Error(result.throwable ?: Exception(result.message))
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PhotoEntity>): PhotoRemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { photo ->
                database.photoRemoteKeyDao().getRemoteKeyByPhotoId(photo.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PhotoEntity>): PhotoRemoteKeyEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { photo ->
                database.photoRemoteKeyDao().getRemoteKeyByPhotoId(photo.id)
            }
    }
}
