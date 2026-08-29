package com.example.composegallery.feature.gallery.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhotos(photos: List<PhotoEntity>): List<Long>

    @Query("UPDATE photos SET pagingOrder = :pagingOrder WHERE id = :photoId")
    suspend fun updatePagingOrder(photoId: String, pagingOrder: Int)

    @Query("SELECT * FROM photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: String): PhotoEntity?

    @Query("SELECT * FROM photos ORDER BY pagingOrder ASC, id ASC")
    fun getPagedPhotos(): PagingSource<Int, PhotoEntity>

    @Query("DELETE FROM photos")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM photos")
    suspend fun getCount(): Int
}
