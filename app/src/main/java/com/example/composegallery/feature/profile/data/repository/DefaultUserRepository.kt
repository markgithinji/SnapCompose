package com.example.composegallery.feature.profile.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.composegallery.core.data.Result
import com.example.composegallery.core.data.model.toDomain
import com.example.composegallery.core.data.model.toDomainModel
import com.example.composegallery.core.data.paging.PagingDefaults
import com.example.composegallery.core.data.safeApiCall
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.model.UnsplashUser
import com.example.composegallery.core.data.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider
import com.example.composegallery.feature.profile.data.paging.UnsplashGetCollectionPhotosPagingSource
import com.example.composegallery.feature.profile.data.paging.UnsplashGetUserCollectionsPagingSource
import com.example.composegallery.feature.profile.data.paging.UnsplashGetUserLikesPagingSource
import com.example.composegallery.feature.profile.data.paging.UnsplashGetUserPhotosPagingSource
import com.example.composegallery.feature.profile.domain.model.PhotoCollection
import com.example.composegallery.feature.profile.domain.model.UserStatistics
import com.example.composegallery.feature.profile.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(
    private val api: UnsplashApi,
    private val stringProvider: StringProvider
) : UserRepository {

    override suspend fun getUserProfile(username: String): Result<UnsplashUser> {
        return safeApiCall(stringProvider) {
            val response = api.getUser(username = username)
            response.toDomainModel()
        }
    }

    override fun getUserPhotos(username: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingDefaults.PAGE_SIZE,
                initialLoadSize = PagingDefaults.INITIAL_LOAD_SIZE,
                prefetchDistance = PagingDefaults.PREFETCH_DISTANCE
            ),
            pagingSourceFactory = {
                UnsplashGetUserPhotosPagingSource(
                    api,
                    username,
                    stringProvider
                )
            }
        ).flow
    }

    override fun getUserCollections(username: String): Flow<PagingData<PhotoCollection>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingDefaults.PAGE_SIZE,
                initialLoadSize = PagingDefaults.INITIAL_LOAD_SIZE,
                prefetchDistance = PagingDefaults.PREFETCH_DISTANCE
            ),
            pagingSourceFactory = {
                UnsplashGetUserCollectionsPagingSource(
                    api,
                    username,
                    stringProvider
                )
            }
        ).flow
    }

    override fun getUserLikedPhotos(username: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingDefaults.PAGE_SIZE,
                initialLoadSize = PagingDefaults.INITIAL_LOAD_SIZE,
                prefetchDistance = PagingDefaults.PREFETCH_DISTANCE
            ),
            pagingSourceFactory = {
                UnsplashGetUserLikesPagingSource(
                    api,
                    username,
                    stringProvider
                )
            }
        ).flow
    }

    override fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingDefaults.PAGE_SIZE,
                initialLoadSize = PagingDefaults.INITIAL_LOAD_SIZE,
                prefetchDistance = PagingDefaults.PREFETCH_DISTANCE
            ),
            pagingSourceFactory = {
                UnsplashGetCollectionPhotosPagingSource(
                    api,
                    collectionId,
                    stringProvider
                )
            }
        ).flow
    }

    override suspend fun getUserStatistics(username: String): Result<UserStatistics> {
        return safeApiCall(stringProvider) {
            val statsDto = api.getUserStatistics(username)
            statsDto.toDomain()
        }
    }
}
