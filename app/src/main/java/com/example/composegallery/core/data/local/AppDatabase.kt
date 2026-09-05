package com.example.composegallery.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composegallery.feature.search.domain.model.RecentSearch
import com.example.composegallery.feature.home.data.local.PhotoDao
import com.example.composegallery.feature.home.data.local.PhotoEntity
import com.example.composegallery.feature.home.data.local.PhotoRemoteKeyDao
import com.example.composegallery.feature.home.data.local.PhotoRemoteKeyEntity
import com.example.composegallery.feature.home.data.local.FavoritePhotoDao
import com.example.composegallery.feature.home.data.local.FavoritePhotoEntity
import com.example.composegallery.feature.home.data.local.TopicCacheMetadataDao
import com.example.composegallery.feature.home.data.local.TopicCacheMetadataEntity
import com.example.composegallery.feature.search.data.local.RecentSearchDao

@Database(
    entities = [
        RecentSearch::class,
        PhotoEntity::class,
        PhotoRemoteKeyEntity::class,
        FavoritePhotoEntity::class,
        TopicCacheMetadataEntity::class
    ],
    version = 6
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun photoDao(): PhotoDao
    abstract fun photoRemoteKeyDao(): PhotoRemoteKeyDao
    abstract fun favoritePhotoDao(): FavoritePhotoDao
    abstract fun topicCacheMetadataDao(): TopicCacheMetadataDao
}
