package com.example.composegallery.feature.gallery.data.di

import android.content.Context
import androidx.room.Room
import com.example.composegallery.feature.gallery.data.local.AppDatabase
import com.example.composegallery.feature.gallery.data.local.RecentSearchDao
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.repository.DefaultGalleryRepository
import com.example.composegallery.feature.gallery.data.repository.DefaultSearchRepository
import com.example.composegallery.feature.gallery.data.repository.DefaultUserRepository
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import com.example.composegallery.feature.gallery.domain.repository.SearchRepository
import com.example.composegallery.feature.gallery.domain.repository.UserRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://api.unsplash.com/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideUnsplashApi(json: Json): UnsplashApi {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(UnsplashApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGalleryRepository(api: UnsplashApi): GalleryRepository {
        return DefaultGalleryRepository(api)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        api: UnsplashApi
    ): UserRepository {
        return DefaultUserRepository(api)
    }

    @Provides
    fun provideSearchRepository(
        api: UnsplashApi,
        recentSearchDao: RecentSearchDao
    ): SearchRepository {
        return DefaultSearchRepository(api, recentSearchDao)
    }
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideRecentSearchDao(db: AppDatabase): RecentSearchDao {
        return db.recentSearchDao()
    }
}
