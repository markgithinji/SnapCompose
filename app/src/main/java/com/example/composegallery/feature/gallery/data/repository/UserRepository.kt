package com.example.composegallery.feature.gallery.data.repository

import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser

interface UserRepository {
    suspend fun getUserProfile(username: String): Result<UnsplashUser>
}