package com.example.composegallery.feature.home.domain.repository

import com.example.composegallery.core.model.Photo
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun addFavorite(photo: Photo)
    suspend fun removeFavorite(photoId: String)
    fun isFavorite(photoId: String): Flow<Boolean>
    suspend fun isFavoriteOneShot(photoId: String): Boolean
    fun getFavorites(): Flow<List<Photo>>
}
