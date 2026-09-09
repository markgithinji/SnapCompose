package com.example.composegallery.feature.search.data

import com.example.composegallery.core.domain.repository.SearchRepository
import com.example.composegallery.feature.search.data.DefaultSearchRepository
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
    abstract fun bindSearchRepository(
        impl: DefaultSearchRepository
    ): SearchRepository
}
