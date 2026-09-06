package com.example.composegallery.core.database.local.home.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.composegallery.core.database.local.home.entity.TopicCacheMetadataEntity

@Dao
interface TopicCacheMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadata(metadata: TopicCacheMetadataEntity)

    @Query("SELECT * FROM topic_cache_metadata WHERE topicId = :topicId")
    suspend fun getMetadata(topicId: String): TopicCacheMetadataEntity?

    @Query("DELETE FROM topic_cache_metadata WHERE topicId = :topicId")
    suspend fun deleteMetadata(topicId: String)
}
