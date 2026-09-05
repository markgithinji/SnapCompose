package com.example.composegallery.feature.photodetail.di

import android.content.Context
import com.example.composegallery.core.util.StringProvider
import com.example.composegallery.feature.photodetail.data.repository.DefaultPhotoActionService
import com.example.composegallery.feature.photodetail.domain.repository.PhotoActionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PhotoDetailModule {

    @Provides
    @Singleton
    fun providePhotoActionService(
        @ApplicationContext context: Context,
        stringProvider: StringProvider
    ): PhotoActionService {
        return DefaultPhotoActionService(context, stringProvider)
    }
}
