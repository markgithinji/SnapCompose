package com.example.composegallery.core.database.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

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
