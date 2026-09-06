package com.example.composegallery.core.repository

import androidx.paging.PagingData
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.model.PhotoCollection
import com.example.composegallery.core.common.Result
import com.example.composegallery.core.model.UnsplashUser
import com.example.composegallery.core.model.UserStatistics
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(username: String): Result<UnsplashUser>
    fun getUserPhotos(username: String): Flow<PagingData<Photo>>
    fun getUserLikedPhotos(username: String): Flow<PagingData<Photo>>
    fun getUserCollections(username: String): Flow<PagingData<PhotoCollection>>
    fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>>
    suspend fun getUserStatistics(username: String): Result<UserStatistics>
}
