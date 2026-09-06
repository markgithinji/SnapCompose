package com.example.composegallery.feature.home.data

import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.database.local.FavoritePhotoDao
import com.example.composegallery.core.database.local.toDomainModel
import com.example.composegallery.core.database.local.toFavoriteEntity
import com.example.composegallery.core.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultFavoriteRepository @Inject constructor(
    private val favoritePhotoDao: FavoritePhotoDao
) : FavoriteRepository {

    override suspend fun addFavorite(photo: Photo) {
        favoritePhotoDao.insertFavorite(photo.toFavoriteEntity())
    }

    override suspend fun removeFavorite(photoId: String) {
        favoritePhotoDao.deleteFavorite(photoId)
    }

    override fun isFavorite(photoId: String): Flow<Boolean> {
        return favoritePhotoDao.isFavorite(photoId)
    }

    override suspend fun isFavoriteOneShot(photoId: String): Boolean {
        return favoritePhotoDao.isFavoriteOneShot(photoId)
    }

    override fun getFavorites(): Flow<List<Photo>> {
        return favoritePhotoDao.getAllFavorites().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}
