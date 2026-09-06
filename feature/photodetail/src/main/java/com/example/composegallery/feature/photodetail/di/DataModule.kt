package com.example.composegallery.feature.photodetail.di

import com.example.composegallery.core.repository.PhotoActionService
import com.example.composegallery.feature.photodetail.data.DefaultPhotoActionService
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
    abstract fun bindPhotoActionService(
        impl: DefaultPhotoActionService
    ): PhotoActionService
}
