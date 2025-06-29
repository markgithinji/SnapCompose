package com.example.composegallery.feature.gallery.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.composegallery.BuildConfig
import com.example.composegallery.feature.gallery.data.model.toDomain
import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.pagingsource.UnsplashGetCollectionPhotosPagingSource
import com.example.composegallery.feature.gallery.data.pagingsource.UnsplashGetUserCollectionsPagingSource
import com.example.composegallery.feature.gallery.data.pagingsource.UnsplashGetUserLikesPagingSource
import com.example.composegallery.feature.gallery.data.pagingsource.UnsplashGetUserPhotosPagingSource
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.data.util.safeApiCall
import com.example.composegallery.feature.gallery.domain.model.Collection
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import com.example.composegallery.feature.gallery.domain.model.UserStatistics
import com.example.composegallery.feature.gallery.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(
    private val api: UnsplashApi
) : UserRepository {

    override suspend fun getUserProfile(username: String): Result<UnsplashUser> {
        return safeApiCall {
            val response = api.getUser(
                username = username,
                clientId = BuildConfig.UNSPLASH_API_KEY
            )
            response.toDomainModel()
        }
    }

    override fun getUserPhotos(username: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                UnsplashGetUserPhotosPagingSource(api, username)
            }
        ).flow
    }

    override fun getUserCollections(username: String): Pager<Int, Collection> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                UnsplashGetUserCollectionsPagingSource(api, username)
            }
        )
    }

    override fun getUserLikedPhotos(username: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                UnsplashGetUserLikesPagingSource(api, username)
            }
        ).flow
    }

    override fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                UnsplashGetCollectionPhotosPagingSource(api, collectionId)
            }
        ).flow
    }

    override suspend fun getUserStatistics(username: String): Result<UserStatistics> {
        return safeApiCall {
            val statsDto = api.getUserStatistics(username, clientId = BuildConfig.UNSPLASH_API_KEY)
            statsDto.toDomain()
        }
    }
}