package com.example.composegallery.core.data.di

import com.example.composegallery.core.data.repository.home.DefaultFavoriteRepository
import com.example.composegallery.core.data.repository.home.DefaultGalleryRepository
import com.example.composegallery.core.data.repository.photodetail.DefaultPhotoActionService
import com.example.composegallery.core.data.repository.profile.DefaultUserRepository
import com.example.composegallery.core.data.repository.search.DefaultSearchRepository
import com.example.composegallery.core.repository.FavoriteRepository
import com.example.composegallery.core.repository.GalleryRepository
import com.example.composegallery.core.repository.PhotoActionService
import com.example.composegallery.core.repository.UserRepository
import com.example.composegallery.core.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

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
    
    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        impl: DefaultSearchRepository
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: DefaultUserRepository
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindPhotoActionService(
        impl: DefaultPhotoActionService
    ): PhotoActionService
}
