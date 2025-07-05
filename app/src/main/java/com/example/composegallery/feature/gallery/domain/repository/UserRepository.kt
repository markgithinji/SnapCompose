package com.example.composegallery.feature.gallery.domain.repository

import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.PhotoCollection
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import com.example.composegallery.feature.gallery.domain.model.UserStatistics
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(username: String): Result<UnsplashUser>
    fun getUserPhotos(username: String): Flow<PagingData<Photo>>
    fun getUserLikedPhotos(username: String): Flow<PagingData<Photo>>
    fun getUserCollections(username: String): Flow<PagingData<PhotoCollection>>
    fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>>
    suspend fun getUserStatistics(username: String): Result<UserStatistics>
}
