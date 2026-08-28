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

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoEntity>
    ): MediatorResult {
        Timber.d("PhotoRemoteMediator: loadType=$loadType")
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                remoteKey?.nextPage?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKey = getRemoteKeyForFirstItem(state)
                val prevPage = remoteKey?.prevPage
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                prevPage
            }
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                val nextPage = remoteKey?.nextPage
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                nextPage
            }
        }

        val result = safeApiCall(stringProvider) {
            Timber.d("PhotoRemoteMediator: Fetching photos for page $page")
            api.getPhotos(page = page, perPage = state.config.pageSize)
        }

        return when (result) {
            is Result.Success -> {
                val photos = result.data.mapNotNull { it.toDomainModel() }
                Timber.d("PhotoRemoteMediator: Fetched ${photos.size} photos")
                val endOfPaginationReached = photos.isEmpty()

                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.photoRemoteKeyDao().clearRemoteKeys()
                        // Removed clearAll() to avoid UI flicker/rearranging on refresh.
                        // Items will be overwritten by REPLACE strategy.
                    }

                    val prevPage = if (page == 1) null else page - 1
                    val nextPage = if (endOfPaginationReached) null else page + 1
                    val keys = photos.map {
                        PhotoRemoteKeyEntity(photoId = it.id, prevPage = prevPage, nextPage = nextPage)
                    }

                    database.photoRemoteKeyDao().insertAll(keys)
                    database.photoDao().insertPhotos(photos.mapIndexed { index, photo ->
                        photo.toEntity(pagingOrder = (page - 1) * state.config.pageSize + index)
                    })
                }

                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }

            is Result.Error -> {
                Timber.e("PhotoRemoteMediator: Error fetching photos: ${result.message}")
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

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PhotoEntity>): PhotoRemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { photoId ->
                database.photoRemoteKeyDao().getRemoteKeyByPhotoId(photoId)
            }
        }
    }
}
