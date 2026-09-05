package com.example.composegallery.core.database.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<PhotoRemoteKeyEntity>)

    @Query("SELECT * FROM photo_remote_keys WHERE photoId = :photoId AND topicId = :topicId")
    suspend fun getRemoteKeyByPhotoId(photoId: String, topicId: String): PhotoRemoteKeyEntity?

    @Query("DELETE FROM photo_remote_keys WHERE topicId = :topicId")
    suspend fun clearRemoteKeys(topicId: String)
}
