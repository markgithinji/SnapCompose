package com.example.composegallery.feature.gallery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composegallery.feature.gallery.domain.model.RecentSearch

@Database(entities = [RecentSearch::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
}
