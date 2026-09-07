package com.example.composegallery.feature.profile.data

import com.example.composegallery.core.domain.repository.UserRepository
import com.example.composegallery.feature.profile.data.DefaultUserRepository
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
    abstract fun bindUserRepository(
        impl: DefaultUserRepository
    ): UserRepository
}
