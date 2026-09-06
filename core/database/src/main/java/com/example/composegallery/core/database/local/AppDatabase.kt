package com.example.composegallery.core.database.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.composegallery.core.database.local.home.dao.FavoritePhotoDao
import com.example.composegallery.core.database.local.home.dao.PhotoDao
import com.example.composegallery.core.database.local.home.dao.PhotoRemoteKeyDao
import com.example.composegallery.core.database.local.home.dao.TopicCacheMetadataDao
import com.example.composegallery.core.database.local.home.entity.FavoritePhotoEntity
import com.example.composegallery.core.database.local.home.entity.PhotoEntity
import com.example.composegallery.core.database.local.home.entity.PhotoRemoteKeyEntity
import com.example.composegallery.core.database.local.home.entity.TopicCacheMetadataEntity
import com.example.composegallery.core.database.local.search.dao.RecentSearchDao
import com.example.composegallery.core.database.local.search.entity.RecentSearchEntity

@Database(
    entities = [
        RecentSearchEntity::class,
        PhotoEntity::class,
        PhotoRemoteKeyEntity::class,
        FavoritePhotoEntity::class,
        TopicCacheMetadataEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun photoDao(): PhotoDao
    abstract fun photoRemoteKeyDao(): PhotoRemoteKeyDao
    abstract fun favoritePhotoDao(): FavoritePhotoDao
    abstract fun topicCacheMetadataDao(): TopicCacheMetadataDao
}
