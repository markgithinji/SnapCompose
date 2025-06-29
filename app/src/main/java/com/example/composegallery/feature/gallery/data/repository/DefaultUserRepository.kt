package com.example.composegallery.feature.gallery.data.repository

import com.example.composegallery.BuildConfig
import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.data.util.safeApiCall
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import com.example.composegallery.feature.gallery.domain.repository.UserRepository
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(
    private val api: UnsplashApi
) : UserRepository {

    override suspend fun getUserProfile(username: String): Result<UnsplashUser> {
        return safeApiCall {
            val response = api.getUser(
                username = username,
                clientId = BuildConfig.UNSPLASH_API_KEY
            )
            response.toDomainModel()
        }
    }
}