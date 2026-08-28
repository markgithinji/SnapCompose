package com.example.composegallery.feature.gallery.data.di

import android.content.Context
import androidx.room.Room
import com.example.composegallery.feature.gallery.data.local.AppDatabase
import com.example.composegallery.feature.gallery.data.local.FavoritePhotoDao
import com.example.composegallery.feature.gallery.data.local.RecentSearchDao
import com.example.composegallery.feature.gallery.data.remote.AuthInterceptor
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.repository.DefaultGalleryRepository
import com.example.composegallery.feature.gallery.data.repository.DefaultPhotoActionService
import com.example.composegallery.feature.gallery.data.repository.DefaultSearchRepository
import com.example.composegallery.feature.gallery.data.repository.DefaultUserRepository
import com.example.composegallery.feature.gallery.data.repository.DefaultFavoriteRepository
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import com.example.composegallery.feature.gallery.domain.repository.PhotoActionService
import com.example.composegallery.feature.gallery.domain.repository.SearchRepository
import com.example.composegallery.feature.gallery.domain.repository.UserRepository
import com.example.composegallery.feature.gallery.domain.repository.FavoriteRepository
import com.example.composegallery.feature.gallery.util.DefaultStringProvider
import com.example.composegallery.feature.gallery.util.StringProvider
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 100 MB cache for images and metadata
        val cache = Cache(context.cacheDir, 100L * 1024L * 1024L)

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(loggingInterceptor)
            .cache(cache)
            .build()
    }

    @Provides
    @Singleton
    fun provideUnsplashApi(json: Json, client: OkHttpClient): UnsplashApi {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(UnsplashApi::class.java)
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100L * 1024L * 1024L) // 100 MB
                    .build()
            }
            .respectCacheHeaders(false) // Unsplash headers can be restrictive; we want to cache images longer
            .build()
    }

    // Provide the StringProvider for string resources access
    @Provides
    @Singleton
    fun provideStringProvider(@ApplicationContext context: Context): StringProvider {
        return DefaultStringProvider(context)
    }

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
    fun provideUserRepository(
        api: UnsplashApi,
        stringProvider: StringProvider
    ): UserRepository {
        return DefaultUserRepository(api, stringProvider)
    }

    @Provides
    @Singleton
    fun providePhotoActionService(
        @ApplicationContext context: Context,
        stringProvider: StringProvider
    ): PhotoActionService {
        return DefaultPhotoActionService(context, stringProvider)
    }

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
    @Singleton
    fun provideFavoriteRepository(
        favoritePhotoDao: FavoritePhotoDao
    ): FavoriteRepository {
        return DefaultFavoriteRepository(favoritePhotoDao)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    fun provideRecentSearchDao(db: AppDatabase): RecentSearchDao {
        return db.recentSearchDao()
    }

    @Provides
    fun provideFavoritePhotoDao(db: AppDatabase): FavoritePhotoDao {
        return db.favoritePhotoDao()
    }
}
