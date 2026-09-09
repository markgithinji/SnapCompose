package com.example.composegallery.core.testing

import com.example.composegallery.core.database.local.home.dao.FavoritePhotoDao
import com.example.composegallery.core.database.local.home.entity.FavoritePhotoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeFavoritePhotoDao : FavoritePhotoDao {

    private val favorites = MutableStateFlow<Map<String, FavoritePhotoEntity>>(emptyMap())

    override fun getAllFavorites(): Flow<List<FavoritePhotoEntity>> {
        return favorites.map { it.values.toList().sortedByDescending { it.favoritedAt } }
    }

    override suspend fun insertFavorite(photo: FavoritePhotoEntity) {
        favorites.update { it + (photo.id to photo) }
    }

    override suspend fun deleteFavorite(photoId: String) {
        favorites.update { it - photoId }
    }

    override fun isFavorite(photoId: String): Flow<Boolean> {
        return favorites.map { it.containsKey(photoId) }
    }

    override suspend fun isFavoriteOneShot(photoId: String): Boolean {
        return favorites.value.containsKey(photoId)
    }
}
