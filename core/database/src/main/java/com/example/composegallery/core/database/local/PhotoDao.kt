package com.example.composegallery.core.database.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhotos(photos: List<PhotoEntity>): List<Long>

    @Query("UPDATE photos SET pagingOrder = :pagingOrder WHERE id = :photoId AND topicId = :topicId")
    suspend fun updatePagingOrder(photoId: String, topicId: String, pagingOrder: Int)

    @Query("SELECT * FROM photos WHERE id = :photoId LIMIT 1")
    suspend fun getPhotoById(photoId: String): PhotoEntity?

    @Query("SELECT * FROM photos WHERE topicId = :topicId ORDER BY pagingOrder ASC, id ASC")
    fun getPagedPhotos(topicId: String): PagingSource<Int, PhotoEntity>

    @Query("DELETE FROM photos WHERE topicId = :topicId")
    suspend fun clearAll(topicId: String)

    @Query("SELECT COUNT(*) FROM photos WHERE topicId = :topicId")
    suspend fun getCount(topicId: String): Int
}
