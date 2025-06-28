package com.example.composegallery.feature.gallery.data.remote

import com.example.composegallery.feature.gallery.data.model.SearchResponseDto
import com.example.composegallery.feature.gallery.data.model.UnsplashPhotoDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApi {
    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("client_id") clientId: String
    ): List<UnsplashPhotoDto>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("client_id") clientId: String
    ): SearchResponseDto

    @GET("photos/{id}")
    suspend fun getPhoto(
        @Path("id") photoId: String,
        @Query("client_id") clientId: String
    ): UnsplashPhotoDto
}
