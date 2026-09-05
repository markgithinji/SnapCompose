package com.example.composegallery.feature.home.di

import com.example.composegallery.core.data.local.AppDatabase
import com.example.composegallery.core.data.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider
import com.example.composegallery.feature.home.data.local.FavoritePhotoDao
import com.example.composegallery.feature.home.data.local.PhotoDao
import com.example.composegallery.feature.home.data.local.TopicCacheMetadataDao
import com.example.composegallery.feature.home.data.local.PhotoRemoteKeyDao
import com.example.composegallery.feature.home.data.repository.DefaultFavoriteRepository
import com.example.composegallery.feature.home.data.repository.DefaultGalleryRepository
import com.example.composegallery.feature.home.domain.repository.FavoriteRepository
import com.example.composegallery.feature.home.domain.repository.GalleryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideGalleryRepository(
        api: UnsplashApi,
        database: AppDatabase,
        stringProvider: StringProvider
    ): GalleryRepository {
        return DefaultGalleryRepository(api, database, stringProvider)
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(
        favoritePhotoDao: FavoritePhotoDao
    ): FavoriteRepository {
        return DefaultFavoriteRepository(favoritePhotoDao)
    }

    @Provides
    fun providePhotoDao(db: AppDatabase): PhotoDao {
        return db.photoDao()
    }

    @Provides
    fun provideFavoritePhotoDao(db: AppDatabase): FavoritePhotoDao {
        return db.favoritePhotoDao()
    }
    
    @Provides
    fun provideTopicCacheMetadataDao(db: AppDatabase): TopicCacheMetadataDao {
        return db.topicCacheMetadataDao()
    }

    @Provides
    fun providePhotoRemoteKeyDao(db: AppDatabase): PhotoRemoteKeyDao {
        return db.photoRemoteKeyDao()
    }
}
