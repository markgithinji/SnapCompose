package com.example.composegallery.feature.gallery.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Collection
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(username: String): Result<UnsplashUser>
    fun getUserPhotos(username: String): Flow<PagingData<Photo>>
    fun getUserLikedPhotos(username: String): Flow<PagingData<Photo>>
    fun getUserCollections(username: String): Pager<Int, Collection>
    fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>>
}