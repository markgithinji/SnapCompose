package com.example.composegallery.core.network.di

import android.content.Context
import androidx.room.Room
import com.example.composegallery.core.database.local.AppDatabase
import com.example.composegallery.core.database.local.FavoritePhotoDao
import com.example.composegallery.core.database.local.PhotoDao
import com.example.composegallery.core.database.local.PhotoRemoteKeyDao
import com.example.composegallery.core.database.local.RecentSearchDao
import com.example.composegallery.core.database.local.TopicCacheMetadataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    fun providePhotoDao(db: AppDatabase): PhotoDao = db.photoDao()

    @Provides
    fun provideFavoritePhotoDao(db: AppDatabase): FavoritePhotoDao = db.favoritePhotoDao()

    @Provides
    fun provideTopicCacheMetadataDao(db: AppDatabase): TopicCacheMetadataDao = db.topicCacheMetadataDao()

    @Provides
    fun providePhotoRemoteKeyDao(db: AppDatabase): PhotoRemoteKeyDao = db.photoRemoteKeyDao()

    @Provides
    fun provideRecentSearchDao(db: AppDatabase): RecentSearchDao = db.recentSearchDao()
}
