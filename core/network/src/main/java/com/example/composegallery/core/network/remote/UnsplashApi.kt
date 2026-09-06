package com.example.composegallery.core.network.remote

import com.example.composegallery.core.network.model.SearchResponseDto
import com.example.composegallery.core.network.model.UnsplashCollectionDto
import com.example.composegallery.core.network.model.UnsplashPhotoDto
import com.example.composegallery.core.network.model.UnsplashTopicDto
import com.example.composegallery.core.network.model.UnsplashUserDto
import com.example.composegallery.core.network.model.UserStatisticsDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface UnsplashApi {
    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int
    ): List<UnsplashPhotoDto>

    @GET("topics")
    suspend fun getTopics(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("order_by") orderBy: String = "position"
    ): List<UnsplashTopicDto>

    @GET("topics/{id_or_slug}/photos")
    suspend fun getTopicPhotos(
        @Path("id_or_slug") topicIdOrSlug: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int
    ): List<UnsplashPhotoDto>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int,
        @Query("orientation") orientation: String? = null,
        @Query("color") color: String? = null,
        @Query("order_by") orderBy: String? = null
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

    @GET
    suspend fun triggerDownload(@Url url: String)

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
