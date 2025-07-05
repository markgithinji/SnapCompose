package com.example.composegallery.feature.gallery.data.remote

import com.example.composegallery.feature.gallery.data.model.SearchResponseDto
import com.example.composegallery.feature.gallery.data.model.UnsplashCollectionDto
import com.example.composegallery.feature.gallery.data.model.UnsplashPhotoDto
import com.example.composegallery.feature.gallery.data.model.UnsplashUserDto
import com.example.composegallery.feature.gallery.data.model.UserStatisticsDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApi {
    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int
    ): List<UnsplashPhotoDto>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int
    ): SearchResponseDto

    @GET("photos/{id}")
    suspend fun getPhoto(@Path("id") photoId: String): UnsplashPhotoDto

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): UnsplashUserDto

    @GET("users/{username}/photos")
    suspend fun getUserPhotos(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int
    ): List<UnsplashPhotoDto>

    @GET("users/{username}/collections")
    suspend fun getUserCollections(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int
    ): List<UnsplashCollectionDto>

    @GET("users/{username}/likes")
    suspend fun getUserLikedPhotos(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int
    ): List<UnsplashPhotoDto>

    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotos(
        @Path("id") collectionId: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<UnsplashPhotoDto>

    @GET("users/{username}/statistics")
    suspend fun getUserStatistics(@Path("username") username: String): UserStatisticsDto

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
