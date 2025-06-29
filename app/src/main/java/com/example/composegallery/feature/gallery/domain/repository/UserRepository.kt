package com.example.composegallery.feature.gallery.domain.repository

import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Collection
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser

interface UserRepository {
    suspend fun getUserProfile(username: String): Result<UnsplashUser>
    suspend fun getUserPhotos(username: String): Result<List<Photo>>
    suspend fun getUserCollections(username: String): Result<List<Collection>>
}