package com.example.composegallery.feature.profile.di

import com.example.composegallery.core.data.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider
import com.example.composegallery.feature.profile.data.repository.DefaultUserRepository
import com.example.composegallery.feature.profile.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        api: UnsplashApi,
        stringProvider: StringProvider
    ): UserRepository {
        return DefaultUserRepository(api, stringProvider)
    }
}
