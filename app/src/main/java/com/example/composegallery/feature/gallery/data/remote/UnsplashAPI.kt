package com.example.composegallery.feature.gallery.data.remote

import com.example.composegallery.feature.gallery.data.model.UnsplashPhotoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {
    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("client_id") clientId: String
    ): List<UnsplashPhotoDto>
}
