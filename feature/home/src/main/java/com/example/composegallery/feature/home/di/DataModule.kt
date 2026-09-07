package com.example.composegallery.feature.home.di

import com.example.composegallery.core.domain.repository.FavoriteRepository
import com.example.composegallery.core.domain.repository.GalleryRepository
import com.example.composegallery.feature.home.data.DefaultFavoriteRepository
import com.example.composegallery.feature.home.data.DefaultGalleryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindGalleryRepository(
        impl: DefaultGalleryRepository
    ): GalleryRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        impl: DefaultFavoriteRepository
    ): FavoriteRepository
}
