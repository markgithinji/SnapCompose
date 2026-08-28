package com.example.composegallery.feature.gallery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composegallery.feature.gallery.domain.model.RecentSearch

@Database(
    entities = [
        RecentSearch::class,
        PhotoEntity::class,
        PhotoRemoteKeyEntity::class,
        FavoritePhotoEntity::class
    ],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun photoDao(): PhotoDao
    abstract fun photoRemoteKeyDao(): PhotoRemoteKeyDao
    abstract fun favoritePhotoDao(): FavoritePhotoDao
}
