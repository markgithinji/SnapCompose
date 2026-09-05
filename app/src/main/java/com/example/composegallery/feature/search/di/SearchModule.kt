package com.example.composegallery.feature.search.di

import com.example.composegallery.core.data.local.AppDatabase
import com.example.composegallery.core.data.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider
import com.example.composegallery.feature.search.data.local.RecentSearchDao
import com.example.composegallery.feature.search.data.repository.DefaultSearchRepository
import com.example.composegallery.feature.search.domain.repository.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Provides
    @Singleton
    fun provideSearchRepository(
        api: UnsplashApi,
        recentSearchDao: RecentSearchDao,
        stringProvider: StringProvider
    ): SearchRepository {
        return DefaultSearchRepository(api, recentSearchDao, stringProvider)
    }

    @Provides
    fun provideRecentSearchDao(db: AppDatabase): RecentSearchDao {
        return db.recentSearchDao()
    }
}
