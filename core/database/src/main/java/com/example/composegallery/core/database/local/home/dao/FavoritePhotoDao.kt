package com.example.composegallery.core.database.local.home.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.composegallery.core.database.local.home.entity.FavoritePhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(photo: FavoritePhotoEntity)

    @Query("DELETE FROM favorites WHERE id = :photoId")
    suspend fun deleteFavorite(photoId: String)

    @Query("SELECT * FROM favorites ORDER BY favoritedAt DESC")
    fun getAllFavorites(): Flow<List<FavoritePhotoEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :photoId)")
    fun isFavorite(photoId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :photoId)")
    suspend fun isFavoriteOneShot(photoId: String): Boolean
}
