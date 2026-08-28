package com.example.composegallery.feature.gallery.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)

    @Query("SELECT * FROM photos ORDER BY pagingOrder ASC")
    fun getPagedPhotos(): PagingSource<Int, PhotoEntity>

    @Query("DELETE FROM photos")
    suspend fun clearAll()
}
