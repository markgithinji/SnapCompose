package com.example.composegallery.feature.home.fakes

import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeFavoriteRepository : FavoriteRepository {

    private val favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    private val favoritePhotos = MutableStateFlow<Map<String, Photo>>(emptyMap())

    override fun getFavorites(): Flow<List<Photo>> {
        return favoritePhotos.map { it.values.toList() }
    }

    override fun isFavorite(photoId: String): Flow<Boolean> {
        return favoriteIds.map { it.contains(photoId) }
    }

    override suspend fun isFavoriteOneShot(photoId: String): Boolean {
        return favoriteIds.value.contains(photoId)
    }

    override suspend fun addFavorite(photo: Photo) {
        favoriteIds.update { it + photo.id }
        favoritePhotos.update { it + (photo.id to photo) }
    }

    override suspend fun removeFavorite(photoId: String) {
        favoriteIds.update { it - photoId }
        favoritePhotos.update { it - photoId }
    }
}
